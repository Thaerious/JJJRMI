package ca.frar.jjjrmi;

import static ca.frar.jjjrmi.Global.LOGGER;
import static ca.frar.jjjrmi.Global.VERY_VERBOSE;
import ca.frar.jjjrmi.jsbuilder.JSBuilderException;
import ca.frar.jjjrmi.jsbuilder.JSClassBuilder;
import ca.frar.jjjrmi.jsbuilder.JSParser;
import ca.frar.stream.TemplateVariableReader;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import spoon.Launcher;
import spoon.reflect.CtModel;

/**
 *
 * @author Ed Armstrong
 */
public class Base {

    private String sourceDir;
    private String packageFileName = "packageFile";
    private String destination = "";
    private String packageName = "package";
    private String version = "0.0.0";
    private boolean printXML = false;
    private boolean generateJSON = false; // generate the package.json file
    private boolean generatePackage = false; // generate the package file
    private HashSet<String> includes = new HashSet<>();
    private HashSet<String> excludes = new HashSet<>();

    private JSParser jsParser;

    /**
     * If any classes are included, only included classes will be processed. If
     * no classes are included all classes will be processed.
     *
     * @param className name of the class, .java extension optional
     */
    public void addInclude(String className) {
        if (!className.endsWith(".java")) className += ".java";
        includes.add(className);
    }

    /**
     * Any classes excluded will not be processed.
     *
     * @param className name of the class, .java extension optional
     */
    public void addExclude(String className) {
        if (!className.endsWith(".java")) className += ".java";
        excludes.add(className);
    }

    /**
     * @param sourceDir the sourceDir to set
     */
    public void setSourceDir(String sourceDir) {
        if (!sourceDir.endsWith("/")) {
            this.sourceDir = sourceDir + "/";
        } else {
            this.sourceDir = sourceDir;
        }
    }

    /**
     * @param packageFileName the packageFileName to set
     */
    public void setPackageFileName(String packageFileName) {
        this.packageFileName = packageFileName;
    }

    /**
     * @param destination the destination to set
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * @param packageName the packageName to set
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @param printXML the printXML to set
     */
    public void setPrintXML(boolean printXML) {
        this.printXML = printXML;
    }

    /**
     * @param generateJSON the generateJSON to set
     */
    public void setGenerateJSON(boolean generateJSON) {
        this.generateJSON = generateJSON;
    }

    /**
     * @param generatePackage the generatePackage to set
     */
    public void setGeneratePackage(boolean generatePackage) {
        this.generatePackage = generatePackage;
    }

    public void run() throws MojoExecutionException, MojoFailureException, FileNotFoundException, IOException {
        Launcher launcher = new Launcher();
        getFiles().forEach(f -> launcher.addInputResource(f.toString()));
        jsParser = new JSParser();
        jsParser.setPackageFileName(packageFileName);
        launcher.buildModel();
        CtModel model = launcher.getModel();
        model.processWith(jsParser);
    }

    private List<Path> getFiles() throws IOException {
        if (!new File(this.sourceDir).exists()) {
            LOGGER.error("Source directory does not exist: " + this.sourceDir);
            System.exit(1);
        }

        Stream<Path> files = Files.walk(Paths.get(this.sourceDir));

        return files.filter((f) -> {
            return f.toString().endsWith(".java");
        })
                .filter((f) -> {
                    if (includes.isEmpty()) return true;
                    int index = f.toString().lastIndexOf("/");
                    String filename = f.toString().substring(index + 1);
                    return this.includes.contains(filename);
                })
                .filter((f) -> {
                    int index = f.toString().lastIndexOf("/");
                    String filename = f.toString().substring(index + 1);
                    return !this.excludes.contains(filename);
                })
                .collect(Collectors.toList());
    }

    public static void writeClass(JSClassBuilder<?> jsClassBuilder, String rootPath) throws FileNotFoundException {
        String outPath = String.format("%s/%s.js", rootPath, jsClassBuilder.getSimpleName());
        File outFile = new File(outPath);
        FileOutputStream fos = new FileOutputStream(outFile);
        PrintWriter pw = new PrintWriter(fos);

        pw.print(jsClassBuilder.fullString());
        pw.print(Global.FILE_STAMP);

        DateFormat df = new SimpleDateFormat("yy.MM.dd HH:mm:ss");
        Date dateobj = new Date();
        pw.print("// " + df.format(dateobj) + "\n");

        pw.close();
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
                System.out.println(jsClassBuilder.toXML(0));
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
