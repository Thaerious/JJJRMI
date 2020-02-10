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
        Object[] arrayO = new Object[2];
        arrayO[0] = 0;
        arrayO[1] = 1;
        Integer[] arrayI = (Integer[]) arrayO;
    }    
}