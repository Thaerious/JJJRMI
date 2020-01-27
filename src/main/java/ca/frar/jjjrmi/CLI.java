package ca.frar.jjjrmi;
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
import org.apache.logging.log4j.Level;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import spoon.Launcher;
import spoon.reflect.CtModel;

/**
 * See jjjrmi script in root directory.
 * @author Ed Armstrong
 */
public class CLI {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JJJRMI");
    private String source = ".";
    private String destination = ".";
    private String packageName = "package";
    private String version = "0.0.0";
    private boolean generateJSON = false; /* generate the package.json file */
    private boolean generatePackage = false; /* generate the package file */
    private boolean packageSubDir = false; /* place files in package subdirectory */
    private String packageFileName = "packageFile";

    private JSParser jsParser;

    public static void main(String... args) throws MojoExecutionException, MojoFailureException, FileNotFoundException, JSBuilderException, IOException {
        CLI cli = new CLI();
        System.out.println("JJJRMI CLI");
        cli.parseArgs(args);
        cli.run();
        cli.output();
    }
    
    public void parseArgs(String ... args){
        for (int i = 0; i < args.length; i++){
            String s = args[i];
            System.out.println(s);
            switch(s){
                case "--json":
                    generateJSON = true;
                    break;
                case "--package":
                    generatePackage = true;
                    break;
                case "-p":
                    if (args[i+1].charAt(0) != '-') packageName = args[i + 1];
                    packageSubDir = true;
                    break;
                case "-i":                    
                    source = args[i + 1];
                    break;
                case "-o":
                    destination = args[i + 1];
                    break;
            }
        }
    }

    public void run() throws MojoExecutionException, MojoFailureException, FileNotFoundException {
        Launcher launcher = new Launcher();
        launcher.addInputResource(source);
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
        System.out.println(destination);
        LOGGER.info("Javascript Code Generator: Generating output");
        String rootPath;
        
        if (this.packageSubDir) rootPath =  String.format("%s/%s", destination, packageName);
        else rootPath =  String.format("%s", destination);
        new File(rootPath).mkdirs();

        for (JSClassBuilder<?> jsClassBuilder : jsParser.jsClassBuilders()) {
            LOGGER.info("file: " + jsClassBuilder.getSimpleName() + ".js");
            String outPath = String.format("%s/%s.js", rootPath, jsClassBuilder.getSimpleName());
            File outFile = new File(outPath);
            FileOutputStream fos = new FileOutputStream(outFile);
            PrintWriter pw = new PrintWriter(fos);

            pw.print(jsClassBuilder.fullString());
            pw.close();
        }

        if (this.generatePackage == true) {
            LOGGER.info("Creating packageFile.js file.");
            this.buildPackageJS();
        } else {
            LOGGER.log(Level.forName("VERY-VERBOSE", 475), "Skipping package js file.");
        }

        if (this.generateJSON == true) {
            LOGGER.info("Creating package.json file.");
            this.copyPackageJSON();
        } else {
            LOGGER.log(Level.forName("VERY-VERBOSE", 475), "Skipping package json file.");
        }
    }

    /**
     * Generate the JavaScript file that contains module class declarations
     * (packageFile.js).
     *
     * @throws FileNotFoundException
     */
    private void buildPackageJS() throws FileNotFoundException {
        String pkgOutPath = String.format("%s/%s/%s.js", this.destination, this.packageName, this.packageFileName);
        File packageOutFile = new File(pkgOutPath);
        PrintWriter packagePW = new PrintWriter(new FileOutputStream(packageOutFile));

        packagePW.print("\"use strict;\";\n");
        packagePW.print(String.format("let %s = {};\n", this.packageName));

        for (JSClassBuilder<?> jsClassBuilder : jsParser.jsClassBuilders()) {
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
