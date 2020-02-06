package ca.frar.jjjrmi.demo;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.testableclasses.HasHandler;
import ca.frar.jjjrmi.translator.encoder.AHandler;
import ca.frar.jjjrmi.translator.HandlerFactory;
import ca.frar.jjjrmi.translator.Translator;
import ca.frar.jjjrmi.translator.encoder.EncodedResult;
import java.io.IOException;

public class Demo {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JJJRMI");    
    static Translator translator = new Translator();
    
    public static void main(String ... args) throws JJJRMIException, IOException{
        EncodedResult encoded = translator.encode(new HasHandler(2, 1.3f));
        System.out.println(encoded.toString(2));
        System.out.println(encoded.length());
    }    
}