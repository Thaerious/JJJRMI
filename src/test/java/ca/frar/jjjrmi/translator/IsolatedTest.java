package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.translator.encoder.EncodedResult;
import ca.frar.jjjrmi.testclasses.Has;
import ca.frar.jjjrmi.testclasses.HasHandler;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author edward
 */
public class IsolatedTest {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JJJRMI");
 
    public static void main(String ... args) throws JJJRMIException{
        new IsolatedTest().test_run();
    }
    
    /**
     * Has handler z = x * y, after decode z = x + y;
     * @throws JJJRMIException 
     */
    @Test
    public void test_run() throws JJJRMIException {
        Translator translator = new Translator();
        HasHandler object = new HasHandler(2, 5);
        EncodedResult encoded = translator.encode(object);
        translator.clear();
        HasHandler decoded = (HasHandler) translator.decode(encoded);
        assertEquals(7, decoded.z);
    }      
    
}