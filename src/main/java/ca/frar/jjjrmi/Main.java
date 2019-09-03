package ca.frar.jjjrmi;
import ca.frar.jjjrmi.jsbuilder.JSBuilderException;
import ca.frar.jjjrmi.jsbuilder.JSClassBuilder;
import ca.frar.jjjrmi.jsbuilder.JSParser;
import ca.frar.stream.TemplateVariableReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import org.apache.logging.log4j.Level;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.compiler.Environment;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

public class Main implements Runnable {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(Main.class);
    private final FactoryImpl factory;
    private final List<Processor<? extends CtElement>> processors = new ArrayList<>();
    private final JSParser jsParser;
    private final RuntimeOptions runtimeOptions;       
    private static final String JS_PACKAGE_FILENAME = "packageFile.js";
    
    public static void main(String... args) {
        try {            
            Launcher.LOGGER.setLevel(org.apache.log4j.Level.OFF);
            new Main(args).run();
        } catch (Exception ex) {
            LOGGER.catching(ex);
        }
    }

    public Main(String... args) {
        this(new RuntimeOptions(args));
    }

    public Main(RuntimeOptions runtimeOptions) {
        Launcher.LOGGER.setLevel(org.apache.log4j.Level.OFF);
        jsParser = new JSParser();
        jsParser.setPackageFileName(runtimeOptions.getPackageFileName());
        this.factory = new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment());
        this.runtimeOptions = runtimeOptions;
    }

    public JSParser getParser(){
        return this.jsParser;
    }
    
    public void run() {
        try {
            LOGGER.info("Javascript Code Generator: Parsing files");
            processors.add(jsParser);
            Environment environment = factory.getEnvironment();
            environment.setNoClasspath(true);
            environment.setCommentEnabled(true);
            environment.setShouldCompile(false);
            environment.setComplianceLevel(8);

            SpoonModelBuilder spoonModelBuilder = new JDTBasedSpoonCompiler(factory);
            File src = new File(runtimeOptions.getInputDirectory());
            
            if (!src.exists()){
                throw new IOException("Input directory not found: " + runtimeOptions.getInputDirectory());
            }
            
            spoonModelBuilder.addInputSource(src);
            
            try{
                spoonModelBuilder.setSourceClasspath(this.runtimeOptions.getClasspath());
            } catch (spoon.compiler.InvalidClassPathException ex){
                LOGGER.warn(ex.getMessage());
            }
            spoonModelBuilder.build();
            spoonModelBuilder.process(processors);

            this.outputClasses();
            if (this.runtimeOptions.isGeneratePackage()) {
                LOGGER.info("Creating packageFile.js file.");
                this.buildPackageJS();
            } else {
                LOGGER.log(Level.forName("VERY-VERBOSE", 475), "Skipping package js file.");
            }

            LOGGER.info(this.runtimeOptions.isGenerateJSON() + " this.runtimeOptions.isGenerateJSON()");
            
            if (this.runtimeOptions.isGenerateJSON()) {
                LOGGER.info("Creating package.json file.");
                this.copyPackageJSON();
            } else {
                LOGGER.log(Level.forName("VERY-VERBOSE", 475), "Skipping package json file.");
            }
        } catch (IOException | JSBuilderException ex) {
            LOGGER.catching(ex);
        }
    }

    private String headerline(int len, String string) {
        int diff = len - string.length();
        if (diff < 0) diff = 0;
        String newString = string + new String(new char[diff]).replace("\0", " ");
        newString = "| " + newString + " |";
        return newString;
    }

    public void printHeader() throws IOException {
        Properties versionProperties = new Properties();
        versionProperties.load(this.getClass().getClassLoader().getResourceAsStream("version.properties"));

        LOGGER.info("+===============================================+");
        LOGGER.info(headerline(45, "JJJ Javascript Code Generator"));
        LOGGER.info("+===============================================+");
    }

    /**
     * Output js classes, one per file.
     * @throws FileNotFoundException
     * @throws JSBuilderException 
     */
    private void outputClasses() throws FileNotFoundException, JSBuilderException {
        LOGGER.info("Javascript Code Generator: Generating output");
        String rootPath = String.format("%s/%s", runtimeOptions.getOutputDirectory(), runtimeOptions.getPackageName());
        new File(rootPath).mkdirs();

        for (JSClassBuilder<?> jsClassBuilder : jsParser.jsClassBuilders()) {
            LOGGER.info("file: " + jsClassBuilder.getSimpleName() + ".js");
            String outPath = String.format("%s/%s/%s.js", runtimeOptions.getOutputDirectory(), runtimeOptions.getPackageName(), jsClassBuilder.getSimpleName());
            File outFile = new File(outPath);
            FileOutputStream fos = new FileOutputStream(outFile);
            PrintWriter pw = new PrintWriter(fos);

            pw.print(jsClassBuilder.fullString());
            pw.close();
        }

    }

    /**
     * Generate the JavaScript file that contains module class declarations (packageFile.js).
     * @throws FileNotFoundException 
     */
    private void buildPackageJS() throws FileNotFoundException {
        String packageName = runtimeOptions.getPackageName();
        String pkgOutPath = String.format("%s/%s/%s", runtimeOptions.getOutputDirectory(), packageName, JS_PACKAGE_FILENAME);
        File packageOutFile = new File(pkgOutPath);
        PrintWriter packagePW = new PrintWriter(new FileOutputStream(packageOutFile));
        
        packagePW.print("\"use strict;\";\n");
        packagePW.print(String.format("let %s = {};\n", runtimeOptions.getPackageName()));

        for (JSClassBuilder<?> jsClassBuilder : jsParser.jsClassBuilders()) {
            packagePW.print(String.format("%s.%s = require(\"./%s\");\n", runtimeOptions.getPackageName(), jsClassBuilder.getSimpleName(), jsClassBuilder.getSimpleName()));
        }

        packagePW.print(String.format("\nif (typeof module !== \"undefined\") module.exports = %s;", runtimeOptions.getPackageName()));
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
        
        if (inputStream == null) throw new FileNotFoundException("template package.json not found");
        
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);        
        TemplateVariableReader templateVariableReader = new TemplateVariableReader(inputStreamReader);

        /* set variables to be copied into package.json template */
        templateVariableReader.set("packageName", runtimeOptions.getPackageName());
        templateVariableReader.set("version", runtimeOptions.getVersion());
        templateVariableReader.set("packageFileName", Main.JS_PACKAGE_FILENAME);

        String packageJsonPath = String.format("%s/%s/package.json", runtimeOptions.getOutputDirectory(), runtimeOptions.getPackageName());
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
