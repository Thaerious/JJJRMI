package ca.frar.jjjrmi;

import java.io.IOException;
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
        base.addSourceDir(this.source);
        base.setDestination(this.destination + "/" + this.packageName);
        base.setGenerateJSON(this.generateJSON);
        base.setGeneratePackage(this.generatePackage);
        base.setPackageFileName(this.packageFileName);
        base.setVersion(this.version);
        base.setPackageName(this.packageName);
        
        for (String s : includeFiles) base.addInclude(s);
        for (String s : excludeFiles) base.addExclude(s);
    }
}

