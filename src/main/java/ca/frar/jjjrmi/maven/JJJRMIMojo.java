package ca.frar.jjjrmi.maven;
import ca.frar.jjjrmi.Main;
import ca.frar.jjjrmi.RuntimeOptions;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name="generate-js", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class JJJRMIMojo extends AbstractMojo {
    private final Log LOGGER = super.getLog();
    
    @Parameter private String source = "src/main/java";
    @Parameter private String destination = "target/jjjrmi";
    @Parameter private String packageName = "package";
    @Parameter private String version = "0.0.0";
    @Parameter private boolean generateJSON = true;
    @Parameter private boolean generatePackage = true;    
    @Parameter private boolean generateSocket = true; 
    @Parameter private String packageFileName = "packageFile";
    
    public void execute() throws MojoExecutionException{
        RuntimeOptions runtimeOptions = new RuntimeOptions();
        runtimeOptions.setInputDirectory(source);
        runtimeOptions.setOutputDirectory(destination);
        runtimeOptions.setPackageName(packageName);
        runtimeOptions.setVersion(version);
        runtimeOptions.setGenerateJSON(generateJSON);
        runtimeOptions.setGeneratePackage(generatePackage);
        runtimeOptions.setGenerateSocket(generateSocket);
        runtimeOptions.setPackageFileName(packageFileName);
        
        Main main = new Main(runtimeOptions);
        main.run();
    }
}
