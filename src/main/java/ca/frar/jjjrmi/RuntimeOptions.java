package ca.frar.jjjrmi;

import java.util.ArrayList;

@SuppressWarnings("PackageVisibleField")
public class RuntimeOptions {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(RuntimeOptions.class);
    private String inputDirectory = "src/main/java";
    private String outputDirectory = "target/jjjrmi";
    private String packageName = "package";
    private String version = "0.0.0";
    private boolean generateJSON = true;
    private boolean generatePackage = true;    
    private int verbosity = 1;
    private ArrayList<String> classpath = new ArrayList<>();

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public RuntimeOptions(String... args) {
        this.processArguments(args);
    }

    protected void processArguments(String... args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "-i":
                    /* the input path to look for java files */
                    if (i < args.length - 1) {
                        this.inputDirectory = args[i + 1];
                    }
                    break;
                case "-o":
                    /* the output root directory default "node_modules" */
                    if (i < args.length - 1) {
                        this.outputDirectory = args[i + 1];
                    }
                    break;
                case "-p":
                    /* the name of the package (required) */
                    if (i < args.length - 1) {
                        this.packageName = args[i + 1];
                    }
                    break;
                case "-v":
                    /* version */
                    if (i < args.length - 1) {
                        this.version = args[i + 1];
                    }
                case "-r":
                    /* report level */
                    if (i < args.length - 1) {
                        switch (args[i + 1]) {
                            case "quiet":
                                this.verbosity = 0;
                                break;
                            case "normal":
                                this.verbosity = 1;
                                break;
                            case "verbose":
                                this.verbosity = 2;
                                break;
                        }
                    }
                    break;
                case "--no_json":
                    /* do not generate the package.json file */
                    this.generateJSON = false;
                    break;
                case "--no_pkg":
                    /* do not generate the package.js file */
                    this.setGeneratePackage(false);
                    break;
            }
        }
    }

    /**
     * Set to generate package.json file.
     * @param value 
     */
    public final void setGenerateJSON(Boolean value) {
        this.generateJSON = value;
    }

    /**
     * Poll whether to generate package.json file.
     * @return 
     */
    public final boolean isGenerateJSON() {
        return this.generateJSON;
    }

    /**
     * Set to generate packageFile.js file.
     * @param value 
     */
    public boolean isGeneratePackage() {
        return generatePackage;
    }

    /**
     * Poll whether to generate packageFile.js file.
     * @return 
     */    
    public void setGeneratePackage(boolean generatePackage) {
        this.generatePackage = generatePackage;
    }

    public final String getVersion() {
        return this.version;
    }

    public final void setVersion(String version) {
        this.version = version;
    }

    public final String getInputDirectory() {
        return inputDirectory;
    }

    public final void setInputDirectory(String inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

    public final String getOutputDirectory() {
        return outputDirectory;
    }

    public final void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public final String getPackageName() {
        return packageName;
    }

    public final void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public final int getVerbosity() {
        return this.verbosity;
    }

    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("inputDirectory: ").append(inputDirectory).append("\n");
        builder.append("outputDirectory: ").append(outputDirectory).append("\n");
        builder.append("packageName: ").append(packageName).append("\n");
        builder.append("verbosity: ").append(verbosity).append("\n");
        return builder.toString();
    }

    public final void addToClasspath(String path) {
        classpath.add(path);
    }

    public final String[] getClasspath() {
        String[] classPathArray = new String[classpath.size()];
        classpath.toArray(classPathArray);
        return classPathArray;
    }
}
