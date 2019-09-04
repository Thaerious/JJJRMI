package ca.frar.jjjrmi.test.translator;

import ca.frar.jjjrm.test.jsportal.JSExec;
import ca.frar.jjjrmi.test.testable.Parent;
import ca.frar.jjjrmi.translator.DecoderException;
import ca.frar.jjjrmi.translator.EncodedJSON;
import ca.frar.jjjrmi.translator.EncoderException;
import ca.frar.jjjrmi.translator.Translator;
import java.io.IOException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Demo {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(Demo.class);    
    
    public static void main(String ... args) throws IllegalArgumentException, IllegalAccessException, EncoderException, IOException, DecoderException{
        Configurator.setLevel(Translator.class.getCanonicalName(), Level.DEBUG);
        
        Translator translator = new Translator();
        EncodedJSON encodedJava = translator.encode(1);        
        
        JSExec jsExec = new JSExec();
        jsExec.start("src/test/js/Translator.js");
        jsExec.writeLine(encodedJava.toString());
        String encodedJS = jsExec.stop();
        LOGGER.info("'" + encodedJS + "'");
//        Object decoded = translator.decode(encodedJS);
    }    
}