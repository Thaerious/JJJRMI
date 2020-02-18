package ca.frar.jjjrmi;

import static ca.frar.jjjrmi.Global.LOGGER;
import static ca.frar.jjjrmi.Global.VERBOSE;
import static ca.frar.jjjrmi.Global.VERY_VERBOSE;
import ca.frar.jjjrmi.jsbuilder.JSBuilderException;
import ca.frar.jjjrmi.jsbuilder.JSClassBuilder;
import ca.frar.jjjrmi.jsbuilder.JSParser;
import ca.frar.stream.TemplateVariableReader;
import edu.emory.mathcs.backport.java.util.Arrays;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.apache.logging.log4j.Level.DEBUG;
import static org.apache.logging.log4j.Level.TRACE;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import spoon.Launcher;
import spoon.reflect.CtModel;

/**
 * See jjjrmi script in root directory.
 *
 * @author Ed Armstrong
 */
public class CLI {

    private HashSet<String> includes = new HashSet<>();
    private HashSet<String> excludes = new HashSet<>();
    private String sourceDir = "./";
    private String destination = "";
    private String packageName = "package";
    private String version = "0.0.0";
    private boolean generateJSON = false; // generate the package.json file
    private boolean generatePackage = false; // generate the package file
    private String packageFileName = "packageFile";
    private boolean printXML = false;
    private JSParser jsParser;
    private String xmlClass = "";

    public static void main(String... args) throws MojoExecutionException, MojoFailureException, FileNotFoundException, JSBuilderException, IOException {
        CLI cli = new CLI();
        LOGGER.info("JJJRMI CLI");
        cli.parseArgs(args);
        cli.run();
        cli.output();
    }

    public void parseArgs(String... args) {
        @SuppressWarnings("unchecked")
        List<String> argList = new LinkedList<>();
        for (String s : args) argList.add(s);

        while (!argList.isEmpty()) {
            parse(argList);
        }
    }

    public void parse(List<String> argList) {
        String s = argList.remove(0);

        switch (s) {
            case "-j":
            case "--json":
                generateJSON = true;
                break;
            case "-p":
            case "--package":
                generatePackage = true;
                break;
            case "-n":
            case "--name":
                packageName = argList.remove(0);
                break;
            case "-i":
            case "--include":
                while (!argList.isEmpty() && argList.get(0).charAt(0) != '-') {
                    String className = argList.remove(0);
                    if (!className.endsWith(".java")) className += ".java";
                    includes.add(className);
                }
                break;
            case "-e":
            case "--exclude":
                while (!argList.isEmpty() && argList.get(0).charAt(0) != '-') {
                    String className = argList.remove(0);
                    if (!className.endsWith(".java")) className += ".java";
                    excludes.add(className);
                }
                break;
            case "-d":
            case "--dir":
                sourceDir = argList.remove(0);
                if (!sourceDir.endsWith("/")) {
                    sourceDir = sourceDir + "/";
                }
                break;
            case "-o":
            case "--output":
                /* this will override the default destination */
                destination = argList.remove(0);
                break;
            case "--xml":
                printXML = true;
                break;
            case "-v":
                Configurator.setRootLevel(VERBOSE);
                break;
            case "-vv":
                Configurator.setRootLevel(VERY_VERBOSE);
                break;
            case "-vvv":
                Configurator.setRootLevel(DEBUG);
                break;
            case "-vvvv":
                Configurator.setRootLevel(TRACE);
                break;                  
        }
    }

    private List<Path> getFiles() throws IOException{
        Stream<Path> files = Files.walk(Paths.get(this.sourceDir));
        
        return files.filter((f)->{
            return f.toString().endsWith(".java");
        })
        .filter((f)->{
            if (includes.isEmpty()) return true;
            int index = f.toString().lastIndexOf("/");
            String filename = f.toString().substring(index + 1);            
            return this.includes.contains(filename);
        })
        .filter((f)->{
            int index = f.toString().lastIndexOf("/");
            String filename = f.toString().substring(index + 1);
            return !this.excludes.contains(filename);
        })
        .collect(Collectors.toList());
    }
    
    public void run() throws MojoExecutionException, MojoFailureException, FileNotFoundException, IOException {                
        Launcher launcher = new Launcher();
        getFiles().forEach(f->launcher.addInputResource(f.toString()));
        jsParser = new JSParser();
        jsParser.setPackageFileName(packageFileName);
        launcher.buildModel();
        CtModel model = launcher.getModel();
        model.processWith(jsParser);
    }

    /**
     * Output js classes, one per file.
     *
     * @throws FileNotFoundException
     * @throws JSBuilderException
     */
    public void output() throws FileNotFoundException, JSBuilderException, IOException {
        LOGGER.log(VERY_VERBOSE, "+------------------------------------------------------------------------------+");
        LOGGER.info("Javascript Code Generator: Generating output");
        String rootPath;

        /* if destination is not set use 'target/jjjrmi/packagename' */
        if (destination.isEmpty()) rootPath = String.format("target/jjjrmi/%s", packageName);
        else rootPath = String.format("%s", destination);

        LOGGER.log(VERY_VERBOSE, "Root Path: " + destination);
        new File(rootPath).mkdirs();

        for (JSClassBuilder<?> jsClassBuilder : jsParser.jsClassBuilders()) {
            LOGGER.info("file: " + jsClassBuilder.getSimpleName() + ".js");
            Base.writeClass(jsClassBuilder, rootPath);

            if (this.printXML) {
                if (this.xmlClass.isBlank()) System.out.println(jsClassBuilder.toXML(0));
                else if (this.xmlClass.equals(jsClassBuilder.getSimpleName())) System.out.println(jsClassBuilder.toXML(0));
            }
        }

        if (this.generatePackage == true) {
            LOGGER.info("Creating packageFile.js file.");
            this.buildPackageJS();
        } else {
            LOGGER.log(VERY_VERBOSE, "Skipping package js file.");
        }

        if (this.generateJSON == true) {
            LOGGER.info("Creating package.json file.");
            this.copyPackageJSON();
        } else {
            LOGGER.log(VERY_VERBOSE, "Skipping package json file.");
        }
    }

    /**
     * Generate the JavaScript file that contains module class declarations
     * (packageFile.js).
     *
     * @throws FileNotFoundException
     */
    private void buildPackageJS() throws FileNotFoundException {
        String pkgOutPath = String.format("%s/%s.js", this.destination, this.packageFileName);
        File packageOutFile = new File(pkgOutPath);
        PrintWriter packagePW = new PrintWriter(new FileOutputStream(packageOutFile));

        packagePW.print("\"use strict;\";\n");
        packagePW.print(String.format("let %s = {};\n", this.packageName));

        for (JSClassBuilder<?> jsClassBuilder : jsParser.jsClassBuilders()) {
            if (jsClassBuilder.getOptions().doNotPackage()) continue;
            packagePW.print(String.format("%s.%s = require(\"./%s\");\n", this.packageName, jsClassBuilder.getSimpleName(), jsClassBuilder.getSimpleName()));
        }

        packagePW.print(String.format("\nif (typeof module !== \"undefined\") module.exports = %s;", this.packageName));
        packagePW.close();
    }

    /**
     * Copy the package.json file from the jjjrmi .jar to the output path.
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void copyPackageJSON() throws FileNotFoundException, IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("package.json");

        if (inputStream == null) {
            throw new FileNotFoundException("template package.json not found");
        }

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        TemplateVariableReader templateVariableReader = new TemplateVariableReader(inputStreamReader);

        /* set variables to be copied into package.json template */
        templateVariableReader.set("packageName", this.packageName);
        templateVariableReader.set("version", this.version);
        templateVariableReader.set("packageFileName", this.packageFileName);

        String packageJsonPath = String.format("%s/%s/package.json", this.destination, this.packageName);
        File file = new File(packageJsonPath);

        FileWriter fileWriter = new FileWriter(file);

        char[] cbuf = new char[32];
        int r = 0;
        while ((r = templateVariableReader.read(cbuf, 0, cbuf.length)) != -1) {
            fileWriter.write(cbuf, 0, r);
        }

        fileWriter.close();
    }
}
