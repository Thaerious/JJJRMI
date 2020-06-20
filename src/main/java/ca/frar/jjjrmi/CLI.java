package ca.frar.jjjrmi;

import static ca.frar.jjjrmi.Global.LOGGER;
import static ca.frar.jjjrmi.Global.VERBOSE;
import static ca.frar.jjjrmi.Global.VERY_VERBOSE;

import ca.frar.jjjrmi.jsbuilder.JSBuilderException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public static void main(String... args) throws IOException, ClassNotFoundException {
        try {
            if (args.length == 0){
                printhelp();
            } else {
                CLI cli = new CLI();
                cli.run(args);
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(CLI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run(String ... args) throws IOException, ClassNotFoundException {
        Base base = new Base();
        parseArgs(base, args);
        base.run();
        base.output();
    }

    public void parseArgs(Base base, String... args) throws IOException, MalformedURLException, ClassNotFoundException {
        @SuppressWarnings("unchecked")
        List<String> argList = prepArgs(args);

        while (!argList.isEmpty()) {
            parse(base, argList);
        }
    }

    /**
     * Replace any '=' with spaces.
     *
     * @param args
     * @return
     */
    List<String> prepArgs(String... args) {
        /* put args together then reseperate them with spaces */
        StringBuilder builder = new StringBuilder();
        for (String s : args) builder.append(s).append(" "); // create a space delimited string of all arguments.
//        String[] split = builder.toString().split("[= \t]+");

        List<String> argList = new LinkedList<>();
        for (String s : args) {
            String value;

            if (s.indexOf('=') == -1) {
                value = s;
            } else {
                String s1 = s.substring(0, s.indexOf('='));
                value = s.substring(s.indexOf('=') + 1);
                argList.add(s1);
            }

            if (value.startsWith("\"") && value.endsWith("\"")) {
                argList.add(value);
                continue;
            }

            String[] splitString = value.split("[ \t\n]+");
            for (String splitPart : splitString) argList.add(splitPart);
        }
        return argList;
    }

    public static void printhelp(){
        System.out.println("NAME");
        System.out.println("\tjjjrmi - java to js code transpiler\n");
        System.out.println("SYNOPSIS");
        System.out.println("\tjjjrmi [OPTION]...\n");
        System.out.println("DESCRIPTION");
        System.out.println("\tFlags are in lower case. Arguments are in upper case.  The space between the flag and the argument can be any number of spaces, equals(=) or tabs");
        System.out.println("\tOptional arguments are enclosed in square brackets.");
        System.out.println("\n");
        System.out.println("\t-j, --json");
        System.out.println("\t\tgenerate package.json file");
        System.out.println("\n");
        System.out.println("\t-p, --package");
        System.out.println("\t\tgenerate packageFile.js");
        System.out.println("\n");
        System.out.println("\t-n NAME, --name NAME");
        System.out.println("\t\tset package name, defaults to 'package'");
        System.out.println("\n");
        System.out.println("\t-h [JAR|CLASS]..., --handlers [JAR|CLASS]...");
        System.out.println("\t\tspecify .jar or .class files find handlers");
        System.out.println("\n");
        System.out.println("\t-i [CLASSNAME]..., --include [CLASSNAME]");
        System.out.println("\t\ta list of classes to process, if omitted process all, has precedence over exclude");
        System.out.println("\n");
        System.out.println("\t-e [CLASSNAME]..., --exclude [CLASSNAME]...");
        System.out.println("\t\ta list of classes to skip, if omitted skip none");
        System.out.println("\n");
        System.out.println("\t-d [PATH]..., --directory [PATH]...");
        System.out.println("\t\tDirectories in which to search for class files.  All child directories will be searched recursively");
        System.out.println("\n");
        System.out.println("\t-o PATH, --output PATH");
        System.out.println("\t\tpath to write js files to, defaults to target/jjjrmi/PACKAGENAME");
        System.out.println("\n");
        System.out.println("\t--help");
        System.out.println("\t\tdisplay help (this)");
    }

    void parse(Base base, List<String> argList) throws MalformedURLException, ClassNotFoundException, IOException {
        String s = argList.remove(0);

        switch (s) {
            case "--help":
                printhelp();
                break;
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
            case "-h":
            case "--handlers":
                while (!argList.isEmpty() && argList.get(0).charAt(0) != '-') {
                    base.addHandler(argList.remove(0));
                }
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
                while (!argList.isEmpty() && argList.get(0).charAt(0) != '-') {
                    base.addSourceDir(argList.remove(0));
                }
                break;
            case "-o":
            case "--output":
                /* this will override the default destination */
                base.setDestination(argList.remove(0));
                break;

            // debug flags, not docuemented
            case "--xml":
                base.setPrintXML(true);
                break;
            case "--dc":
                base.addDeliminatingComments();
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
