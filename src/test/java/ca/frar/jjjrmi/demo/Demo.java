package ca.frar.jjjrmi.demo;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.testableclasses.Circular;
import ca.frar.jjjrmi.testableclasses.SelfReferential;
import ca.frar.jjjrmi.testableclasses.Simple;
import ca.frar.jjjrmi.translator.encoder.EncodedResult;
import ca.frar.jjjrmi.translator.Translator;
import java.io.IOException;

public class Demo {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JJJRMI");    
    static Translator translator = new Translator();
    
    public static void main(String ... args) throws JJJRMIException, IOException{
//        demo(new Simple());
//        demo(new Primitives());
//        demo(new Parent());
//        demo(new SelfReferential());
//        demo(new Circular());
        
        Simple simple = new Simple();
        demo(simple);
        demo(simple);
    }    
    
    public static void demo(Object object) throws EncoderException{        
        EncodedResult encodedJava = translator.encode(object);        
        LOGGER.info(encodedJava.toString(2));
    }
}