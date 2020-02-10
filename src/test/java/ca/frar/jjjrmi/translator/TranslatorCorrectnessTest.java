package ca.frar.jjjrmi.translator;

import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.exceptions.MissingConstructorException;
import ca.frar.jjjrmi.exceptions.MissingReferenceException;
import ca.frar.jjjrmi.translator.encoder.EncodedResult;
import ca.frar.jjjrmi.testableclasses.ArrayWrapper;
import ca.frar.jjjrmi.testableclasses.Has;
import ca.frar.jjjrmi.testableclasses.MissingConstructor;
import ca.frar.jjjrmi.testableclasses.Primitives;
import ca.frar.jjjrmi.testableclasses.PrimitivesExtended;
import ca.frar.jjjrmi.testableclasses.Simple;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author edward
 */
public class TranslatorCorrectnessTest {

    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JJJRMI");

    /**
     * The same translator can decode the json it created.
     *
     * @throws EncoderException
     */
    @Test
    public void test_simple() throws JJJRMIException {
        Translator translator = new Translator();
        Simple object = new Simple();
        EncodedResult encoded = translator.encode(object);
        Object decoded = translator.decode(encoded);

        assertEquals(Simple.class, decoded.getClass());
        assertEquals(object, decoded);
    }

    /**
     * Changing the local copies values will be maintained.
     *
     * @throws EncoderException
     */
    @Test
    public void test_simple_retain_values() throws JJJRMIException {
        Translator translator = new Translator();
        Has<Integer> object = new Has<>(3);
        EncodedResult encoded = translator.encode(object);
        Object decoded = translator.decode(encoded);
        object.set(7);

        assertEquals(Has.class, decoded.getClass());
        assertEquals(object, decoded);
        assertEquals(7, object.get().intValue());
    }

    /**
     * Object from the another translator will be a different object with the
     * same values.
     *
     * @throws EncoderException
     */
    @Test
    public void test_simple_2() throws JJJRMIException {
        Translator translator1 = new Translator();
        Translator translator2 = new Translator();
        Simple object = new Simple();
        EncodedResult encoded = translator1.encode(object);
        Object decoded = translator2.decode(encoded);

        assertEquals(Simple.class, decoded.getClass());
        assertNotEquals(object, decoded);
    }

    /*
     * When decoded reference is returned.
     */
    @Test
    public void test_array_as_field_00() throws JJJRMIException {
        Translator translator = new Translator();
        ArrayWrapper arrayWrapper = new ArrayWrapper();
        EncodedResult encoded = translator.encode(arrayWrapper);
        ArrayWrapper decoded = (ArrayWrapper) translator.decode(encoded);
        assertEquals(arrayWrapper.hashCode(), decoded.hashCode());
    }

    /*
     * Because the translator was cleared between encode/decode a new object is made.
     */
    @Test
    public void test_array_as_field_02() throws JJJRMIException {
        Translator translator = new Translator();
        ArrayWrapper arrayWrapper = new ArrayWrapper();
        EncodedResult encoded = translator.encode(arrayWrapper);
        translator.clear();

        assertFalse(translator.hasReference("S0"));
        ArrayWrapper decoded = (ArrayWrapper) translator.decode(encoded);
        assertNotEquals(arrayWrapper.hashCode(), decoded.hashCode());
        assertEquals(Arrays.toString(arrayWrapper.arrayField), Arrays.toString(decoded.arrayField));
    }

    @Test
    public void test_primitives() throws JJJRMIException {
        Translator translator = new Translator();
        Primitives object = new Primitives(9);
        EncodedResult encoded = translator.encode(object);
        translator.clear();

        Primitives decoded = (Primitives) translator.decode(encoded);
        assertTrue(object.equals(decoded));
    }

    @Test
    public void test_primitives_from_super() throws JJJRMIException {
        Translator translator = new Translator();
        PrimitivesExtended object = new PrimitivesExtended(9);
        EncodedResult encoded = translator.encode(object);
        translator.clear();

        Primitives decoded = (PrimitivesExtended) translator.decode(encoded);
        assertTrue(object.equals(decoded));
    }

    @Test
    public void test_missing_constructor() throws JJJRMIException {
        assertThrows(MissingConstructorException.class, () -> {
            Translator translator = new Translator();
            MissingConstructor object = new MissingConstructor(5);
            EncodedResult encoded = translator.encode(object);
            translator.clear();
            translator.decode(encoded);
        });
    }

    @Test
    public void test_null() throws JJJRMIException {
        Translator translator = new Translator();
        Has<Object> object = new Has<>(null);
        EncodedResult encoded = translator.encode(object);
        translator.clear();

        Has<Object> decoded = (Has<Object>) translator.decode(encoded);
        assertEquals(null, decoded.get());
    }
    
    @Test
    public void test_reference_as_root() throws JJJRMIException {
        Translator translator = new Translator();
        Has<Object> object = new Has<>(null);
        translator.encode(object);
        EncodedResult encoded = translator.encode(object);
        Object decoded = translator.decode(encoded);
        assertEquals(object, decoded);
    }
    
    /**
     * The wrapping object is new, the wrapped object is the same.
     * @throws JJJRMIException 
     */
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
        
    @Test
    public void test_array_of_empty() throws JJJRMIException {
        Translator translator = new Translator();
        boolean[] array = new boolean[0];
        Has<boolean[]> object = new Has<>(array);
        EncodedResult encoded = translator.encode(object);
        translator.clear();
        translator.decode(encoded);
    }
            
//    /**
//     * This test depends on the encoding to run.
//     */
//    @Test
//    public void test_missing_root_reference() throws JJJRMIException {
//        assertThrows(MissingReferenceException.class, () -> {
//            Translator translator = new Translator();
//            MissingConstructor object = new MissingConstructor(5);
//            translator.encode(object);
//            EncodedResult encoded = translator.encode(object);
//            encoded.setRoot("BROKEN");
//            translator.decode(encoded);
//        });
//    }
}
