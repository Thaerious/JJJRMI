package ca.frar.jjjrmi;

import static ca.frar.jjjrmi.Global.VERBOSE;
import static ca.frar.jjjrmi.Global.VERY_VERBOSE;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.apache.logging.log4j.Level.DEBUG;
import static org.apache.logging.log4j.Level.INFO;
import static org.apache.logging.log4j.Level.TRACE;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

@Mojo(name = "generate-js", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class MOJO extends AbstractMojo {

    private final Log LOGGER = getLog();
    @Parameter private String reportLevel = "INFO";
    @Parameter private String source = "src/main/java";
    @Parameter private String destination = null;
    @Parameter private String packageName = "package";
    @Parameter private String version = "0.0.0";
    @Parameter private boolean generateJSON = true;
    @Parameter private boolean generatePackage = true;
    @Parameter private String packageFileName = "packageFile.js";
    @Parameter private String[] includes = new String[0];
    @Parameter private String[] excludes = new String[0];
    @Parameter private String[] handlers = new String[0];
    @Parameter(defaultValue = "${project}", required = true, readonly = true) MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            Base base = new Base();
            Global.header(INFO, "JJJRMI MOJO");            
            fillArguments(base);            
            base.run();
            base.output();
        } catch (ClassNotFoundException | IOException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }

    public void fillArguments(Base base) throws ClassNotFoundException, IOException {
        if (this.destination == null) this.destination = "target/jjjrmi/" + this.packageName;

        switch (this.reportLevel.toUpperCase()) {
            case "VERBOSE":
                Configurator.setRootLevel(VERBOSE);
                break;
            case "VERY VERBOSE":
                Configurator.setRootLevel(VERY_VERBOSE);
                break;
            case "DEBUG":
                Configurator.setRootLevel(DEBUG);
                break;
            case "TRACE":
                Configurator.setRootLevel(TRACE);
                break;
        }

        base.addSourceDir(this.source);
        base.setDestination(this.destination);
        base.setGenerateJSON(this.generateJSON);
        base.setGeneratePackage(this.generatePackage);
        base.setPackageFileName(this.packageFileName);
        base.setVersion(this.version);
        base.setPackageName(this.packageName);

        base.printInfo();
        
        for (String s : includes) base.addInclude(s);
        for (String s : excludes) base.addExclude(s);
        for (String s : handlers) base.addHandler(s);
    }
}
