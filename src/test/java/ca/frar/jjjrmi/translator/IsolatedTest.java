package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.testclasses.Has;
import ca.frar.jjjrmi.testclasses.HasHandler;
import ca.frar.jjjrmi.testclasses.Simple;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author edward
 */
public class IsolatedTest {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JJJRMI");
 
    public static void main(String ... args) throws JJJRMIException{
        new IsolatedTest().test_simple();
    }
    
    /**
     * Has handler z = x * y, after decode z = x + y;
     * @throws JJJRMIException 
     */
    @Test
    public void run() throws JJJRMIException {
        Translator translator = new Translator();
        HasHandler object = new HasHandler(2, 5);
        TranslatorResult encoded = translator.encode(object);
        translator.clear();
        HasHandler decoded = (HasHandler) translator.decode(encoded.toString()).getRoot();
        assertEquals(7, decoded.z);
    }    
    
    @Test
    public void test_simple() throws JJJRMIException {
        Translator translator = new Translator();
        Simple object = new Simple();
        TranslatorResult encoded = translator.encode(object);
        Object decoded = translator.decode(encoded.toString());

        assertEquals(Simple.class, decoded.getClass());
        assertEquals(object, decoded);
    }    
    
}