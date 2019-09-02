package ca.frar.jjjrmi.test.translator;

import ca.frar.jjjrmi.test.testable.Parent;
import ca.frar.jjjrmi.translator.EncodedJSON;
import ca.frar.jjjrmi.translator.EncoderException;
import ca.frar.jjjrmi.translator.Translator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

public class Demo {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(Demo.class);    
    
    public static void main(String ... args) throws IllegalArgumentException, IllegalAccessException, EncoderException{
        Configurator.setLevel(Translator.class.getCanonicalName(), Level.DEBUG);
        
        Translator translator = new Translator();
        Parent parent = new Parent();
        EncodedJSON encode = translator.encode(1);
        LOGGER.info(encode.toString(2));
    }    
}