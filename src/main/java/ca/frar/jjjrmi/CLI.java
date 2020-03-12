package ca.frar.jjjrmi;
import static ca.frar.jjjrmi.Global.LOGGER;
import static ca.frar.jjjrmi.Global.VERBOSE;
import static ca.frar.jjjrmi.Global.VERY_VERBOSE;
import ca.frar.jjjrmi.jsbuilder.JSBuilderException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import static org.apache.logging.log4j.Level.*;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * See jjjrmi script in root directory.
 *
 * @author Ed Armstrong
 */
public class CLI {
    public static void main(String... args) throws MojoExecutionException, MojoFailureException, FileNotFoundException, JSBuilderException, IOException {       
        CLI cli = new CLI();
        Base base = new Base();       
        cli.parseArgs(base, args);
        LOGGER.info("JJJRMI CLI");
        base.run();
        base.output();
    }
    
    public void parseArgs(Base base, String... args) {
        @SuppressWarnings("unchecked")
        List<String> argList = prepArgs(args);

        while (!argList.isEmpty()) {
            parse(base, argList);
        }
    }
    
    /**
     * Replace any '=' with spaces.
     * @param args
     * @return 
     */
    public List<String> prepArgs(String ... args){
        List<String> argList = new LinkedList<>();
        for (String s : args){
            if (s.indexOf('=') == -1){
                argList.add(s);
            } else {
                String s1 = s.substring(0, s.indexOf('='));
                String s2 = s.substring(s.indexOf('=') + 1);
                argList.add(s1);
                argList.add(s2);
            }
            
        }
        return argList;
    }

    public void parse(Base base, List<String> argList) {
        String s = argList.remove(0);

        switch (s) {
            case "-j":
            case "--json":
                base.setGenerateJSON(true);
                break;
            case "-p":
            case "--package":
                base.setGeneratePackage(true);
                break;
            case "-n":
            case "--name":
                base.setPackageName(argList.remove(0));
                break;
            case "-i":
            case "--include":                
                while (!argList.isEmpty() && argList.get(0).charAt(0) != '-') {
                    base.addInclude(argList.remove(0));
                }
                break;
            case "-e":
            case "--exclude":
                while (!argList.isEmpty() && argList.get(0).charAt(0) != '-') {
                    base.addExclude(argList.remove(0));
                }
                break;
            case "-d":
            case "--dir":
                base.setSourceDir(argList.remove(0));
                break;
            case "-o":
            case "--output":
                /* this will override the default destination */
                base.setDestination(argList.remove(0));
                break;
            case "--xml":
                base.setPrintXML(true);
                break;
            case "-s":           
                Configurator.setRootLevel(ERROR);
                break;
            case "-ss":
                Configurator.setRootLevel(OFF);
            case "-v":
                Configurator.setRootLevel(VERBOSE);
                break;
            case "-vv":
                Configurator.setRootLevel(VERY_VERBOSE);
                break;
            case "-vvv":
                Configurator.setRootLevel(DEBUG);
                break;
            case "-vvvv":
                Configurator.setRootLevel(TRACE);
                break;                  
        }
    }
}