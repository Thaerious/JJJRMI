package ca.frar.jjjrmi.demo;

import ca.frar.jjjrm.test.jsportal.JSExec;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.test.testable.gamecontroller.GameController;
import ca.frar.jjjrmi.translator.EncodedJSON;
import ca.frar.jjjrmi.translator.Translator;
import java.io.IOException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

public class Demo {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(Demo.class);    
    
    public static void main(String ... args) throws JJJRMIException, IOException{
        Configurator.setLevel(Translator.class.getCanonicalName(), Level.DEBUG);
        
        Translator translator = new Translator();
        GameController gameController = new GameController(null, 0);
        EncodedJSON encodedJava = translator.encode(gameController);        
        LOGGER.info(encodedJava.toString(2));
    }    
}