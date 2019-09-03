package ca.frar.jjjrmi;

import static ca.frar.jjjrmi.CLI.LOGGER;
import ca.frar.jjjrmi.RuntimeOptions;
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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import spoon.Launcher;
import spoon.MavenLauncher;
import spoon.reflect.CtModel;

@Mojo(name = "generate-js", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class MOJO extends AbstractMojo {

    private final Log LOGGER = super.getLog();
    @Parameter private String source = "src/main/java";
    @Parameter private String destination = "target/jjjrmi";
    @Parameter private String packageName = "package";
    @Parameter private String version = "0.0.0";
    @Parameter private boolean generateJSON = true;
    @Parameter private boolean generatePackage = true;
    @Parameter private boolean generateSocket = true;
    @Parameter private String packageFileName = "packageFile";
    @Parameter private String[] sourceClassPath = new String[0];
    @Parameter(defaultValue = "${project}", required = true, readonly = true) MavenProject project;

    private JSParser jsParser;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        this.debug();
        this.init();
        this.run();

        try {
            this.output();
        } catch (IOException | JSBuilderException ex) {
            Logger.getLogger(MOJO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void debug() {
        LOGGER.debug("source = "  + source);
        LOGGER.debug("destination = " + destination);
        LOGGER.debug("packageName = " + packageName);
        LOGGER.debug("version = " + version);
        LOGGER.debug("generateJSON = " + generateJSON);
        LOGGER.debug("generatePackage = " + generatePackage);
        LOGGER.debug("generateSocket = " + generateSocket);
        LOGGER.debug("packageFileName = " + packageFileName);        
        for (int i = 0; i < sourceClassPath.length; i++){
            LOGGER.debug("sourceClassPath = " + sourceClassPath[i]);
        }
    }

    public void run() {
        Launcher launcher = new Launcher();
        launcher.addInputResource(this.source);
        launcher.getEnvironment().setSourceClasspath(sourceClassPath);
        launcher.buildModel();
        CtModel model = launcher.getModel();
        model.processWith(jsParser);
    }

    public void init() throws MojoExecutionException, MojoFailureException {
        jsParser = new JSParser();
        jsParser.setPackageFileName(packageFileName);
    }

    /**
     * Output js classes, one per file.
     *
     * @throws FileNotFoundException
     * @throws JSBuilderException
     */
    public void output() throws FileNotFoundException, JSBuilderException, IOException {
        LOGGER.info("Javascript Code Generator: Generating output");
        String rootPath = String.format("%s/%s", destination, packageName);
        LOGGER.info("path: " + rootPath);
        new File(rootPath).mkdirs();

        for (JSClassBuilder<?> jsClassBuilder : jsParser.jsClassBuilders()) {
            LOGGER.info("file: " + jsClassBuilder.getSimpleName() + ".js");
            String outPath = String.format("%s/%s/%s.js", destination, packageName, jsClassBuilder.getSimpleName());
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
            LOGGER.debug("Skipping package js file.");
        }

        if (this.generateJSON == true) {
            LOGGER.info("Creating package.json file.");
            this.copyPackageJSON();
        } else {
            LOGGER.debug("Skipping package json file.");
        }
    }

    /**
     * Generate the JavaScript file that contains module class declarations
     * (packageFile.js).
     *
     * @throws FileNotFoundException
     */
    private void buildPackageJS() throws FileNotFoundException {
        String pkgOutPath = String.format("%s/%s/%s", this.destination, this.packageName, this.packageFileName);
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
