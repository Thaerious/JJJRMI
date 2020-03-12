package ca.frar.jjjrmi;

import static ca.frar.jjjrmi.Global.LOGGER;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import spoon.Launcher;
import spoon.reflect.CtModel;

@Mojo(name = "generate-js", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class MOJO extends AbstractMojo {
    private final Log LOGGER = getLog();
    @Parameter private String source = "src/main/java";
    @Parameter private String destination = "target/jjjrmi";
    @Parameter private String packageName = "package";
    @Parameter private String version = "0.0.0";
    @Parameter private boolean generateJSON = true;
    @Parameter private boolean generatePackage = true;
    @Parameter private String packageFileName = "packageFile.js";
    @Parameter private String[] includeFiles = new String[0];
    @Parameter private String[] excludeFiles = new String[0];
    @Parameter(defaultValue = "${project}", required = true, readonly = true) MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            Base base = new Base();
            LOGGER.info("JJJRMI CLI");
            fillArguments(base);
            base.printInfo();
            base.run();
            base.output();
        } catch (IOException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }

    public void fillArguments(Base base){
        base.setSourceDir(this.source);
        base.setDestination(this.destination);
        base.setGenerateJSON(this.generateJSON);
        base.setGeneratePackage(this.generatePackage);
        base.setPackageFileName(this.packageFileName);
        base.setVersion(this.version);
        
        for (String s : includeFiles) base.addInclude(s);
        for (String s : excludeFiles) base.addExclude(s);
    }
}

