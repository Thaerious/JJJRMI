package ca.frar.jjjrmi;

import static ca.frar.jjjrmi.Global.LOGGER;
import static ca.frar.jjjrmi.Global.VERBOSE;
import static ca.frar.jjjrmi.Global.VERY_VERBOSE;
import ca.frar.jjjrmi.jsbuilder.JSBuilderException;
import ca.frar.jjjrmi.jsbuilder.JSClassBuilder;
import ca.frar.jjjrmi.jsbuilder.JSParser;
import ca.frar.jjjrmi.translator.HandlerFactory;
import ca.frar.stream.TemplateVariableReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import static org.apache.logging.log4j.Level.INFO;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import spoon.Launcher;
import spoon.reflect.CtModel;

/**
 *
 * @author Ed Armstrong
 */
public class Base {
    private ArrayList<String> inputDirectories = new ArrayList<>();
    private String packageFileName = "packageFile.js";
    private String destination = "";
    private String packageName = "package";
    private String version = "0.0.0";
    private boolean printXML = false;
    private boolean generateJSON = false; // generate the package.json file
    private boolean generatePackage = false; // generate the package file
    private HashSet<String> includes = new HashSet<>();
    private HashSet<String> excludes = new HashSet<>();
    private JSParser jsParser;

    public void addHandler(String path) throws MalformedURLException, ClassNotFoundException, IOException{        
        if (path.endsWith(".jar")){
            HandlerFactory.getInstance().addJar(new File(path));
        } 
        else {
            HandlerFactory.getInstance().addClasspath(new File(path));
        }
    }
    
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
    public void addSourceDir(String sourceDir) {
        if (!sourceDir.endsWith("/")) {
            this.inputDirectories.add(sourceDir + "/");
        } else {
            this.inputDirectories.add(sourceDir);
        }
    }

    /**
     * @param packageFileName the packageFileName to set
     */
    public void setPackageFileName(String packageFileName) {
        if (packageFileName.endsWith(".js")){
            this.packageFileName = packageFileName;        
        } else {
            this.packageFileName = packageFileName + ".js";        
        }
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

    public void printInfo() {
        for (String s : this.inputDirectories){
            LOGGER.info(Global.line("source = "  + s));
        }        
        LOGGER.info(Global.line("destination = " + this.destination));
        LOGGER.info(Global.line("packageName = " + this.packageName));
        LOGGER.info(Global.line("version = " + this.version));
        LOGGER.info(Global.line("generateJSON = " + this.generateJSON));
        LOGGER.info(Global.line("generatePackage = " + this.generatePackage));
        LOGGER.info(Global.line("packageFileName = " + this.packageFileName));     
    }
    
    public void run() throws FileNotFoundException, IOException {
        Launcher launcher = new Launcher();
        getFiles().forEach(f -> launcher.addInputResource(f.toString()));
        jsParser = new JSParser();
        jsParser.setPackageFileName(packageFileName);
        launcher.buildModel();
        CtModel model = launcher.getModel();
        model.processWith(jsParser);
    }

    private List<Path> getFiles() throws IOException{
        List<Path> filePaths = new ArrayList<Path>();
        for (String s : this.inputDirectories){
            this.getFiles(s, filePaths);
        }
        return filePaths;
    }
    
    private void getFiles(String sourceDir, List<Path> filePaths) throws IOException {
        if (!new File(sourceDir).exists()) {
            LOGGER.error("Source directory does not exist: " + sourceDir);
            System.exit(1);
        }

        Stream<Path> files = Files.walk(Paths.get(sourceDir));

        files.filter((f) -> {
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
            .forEach((p) -> filePaths.add(p));
    }

    private static void writeClass(JSClassBuilder<?> jsClassBuilder, String rootPath) throws FileNotFoundException {
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
        LOGGER.log(VERBOSE, Global.header("Skipping"));
        for(String s : jsParser.getSkippedClasses()){
            LOGGER.log(VERBOSE, Global.line(s));
            LOGGER.log(VERY_VERBOSE, Global.line(" - " + jsParser.getSkippedReason(s)));
        }
        Global.tail(VERBOSE);

        LOGGER.log(INFO, Global.header("Generating output"));

        LOGGER.log(VERBOSE, Global.line("Root Path: " + destination));
        new File(destination).mkdirs();

        for (JSClassBuilder<?> jsClassBuilder : jsParser.jsClassBuilders()) {
            LOGGER.log(INFO, Global.line(jsClassBuilder.getSimpleName() + ".js"));
            Base.writeClass(jsClassBuilder, destination);

            if (this.printXML) {
                System.out.println(jsClassBuilder.toXML(0));
            }
        }

        if (this.generatePackage == true) {
            LOGGER.log(INFO, Global.line("Creating packageFile.js file."));
            this.buildPackageJS();
        } else {
            LOGGER.log(VERY_VERBOSE, Global.line("Skipping package js file."));
        }

        if (this.generateJSON == true) {
            LOGGER.log(INFO, Global.line("Creating package.json file."));
            this.copyPackageJSON();
        } else {
            LOGGER.log(VERY_VERBOSE, Global.line("Skipping package json file."));
        }
        
        Global.tail(INFO);
    }

    /**
     * Generate the JavaScript file that contains module class declarations
     * (packageFile.js).
     *
     * @throws FileNotFoundException
     */
    private void buildPackageJS() throws FileNotFoundException {
        String pkgOutPath = String.format("%s/%s", this.destination, this.packageFileName);
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
    private void copyPackageJSON() throws FileNotFoundException, IOException {
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

        String packageJsonPath = String.format("%s/package.json", this.destination);
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
