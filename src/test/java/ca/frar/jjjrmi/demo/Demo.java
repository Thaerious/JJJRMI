package ca.frar.jjjrmi.demo;

import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.testclasses.HasHandler;
import ca.frar.jjjrmi.translator.encoder.AHandler;
import ca.frar.jjjrmi.translator.HandlerFactory;
import ca.frar.jjjrmi.translator.Translator;
import ca.frar.jjjrmi.translator.encoder.EncodedResult;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

public class Demo {

    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JJJRMI");
    static Translator translator = new Translator();

    public static void main(String... args) throws JJJRMIException, IOException {
        System.out.println("I am java");
        Process exec = Runtime.getRuntime().exec("node src/test/js/test.js");
        
        InputStream inputStream = exec.getInputStream();
        InputStreamReader isr = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(isr);
        
        String line = br.readLine();
        while (line != null) {
            System.out.println(line);
            line = br.readLine();
        }
    }
}
