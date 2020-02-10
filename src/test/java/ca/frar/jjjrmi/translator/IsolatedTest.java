package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.translator.encoder.EncodedResult;
import ca.frar.jjjrmi.testableclasses.Has;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author edward
 */
public class IsolatedTest {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JJJRMI");
 
    public static void main(String ... args) throws JJJRMIException{
        new IsolatedTest().test();
    }
    
    @Test
    public void test() throws EncoderException, DecoderException {
        Translator translator = new Translator();
        boolean[] array = new boolean[0];
        Has<boolean[]> object = new Has<>(array);
        EncodedResult encoded = translator.encode(object);
        translator.clear();
        Has<Object[]> decoded = (Has<Object[]>) translator.decode(encoded);
        assertEquals(0, decoded.get().length);
    }  
    
}