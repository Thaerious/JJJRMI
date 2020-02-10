package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.translator.encoder.EncodedResult;
import ca.frar.jjjrmi.testableclasses.ArrayWrapper;
import ca.frar.jjjrmi.testableclasses.Has;
import ca.frar.jjjrmi.testableclasses.Primitives;
import ca.frar.jjjrmi.testableclasses.PrimitivesExtended;
import ca.frar.jjjrmi.testableclasses.Simple;
import static ca.frar.jjjrmi.translator.TranslatorCorrectnessTest.LOGGER;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author edward
 */
public class IsolatedTest {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JJJRMI");
 
    @Test
    public void test_reference_as_field() throws JJJRMIException {
        Translator translator = new Translator();
        Simple simple = new Simple();
        Has<Simple> has1 = new Has<>(simple);
        translator.encode(has1);
        Has<Simple> has2 = new Has<>(simple);
        EncodedResult encoded = translator.encode(has2); 
        translator.removeByValue(has2);
        Has<Simple> decoded = (Has<Simple>) translator.decode(encoded);
        assertEquals(has1.get(), decoded.get());
    }    
    
}