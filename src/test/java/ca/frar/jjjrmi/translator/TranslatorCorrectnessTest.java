package ca.frar.jjjrmi.translator;

import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.exceptions.MissingConstructorException;
import ca.frar.jjjrmi.exceptions.MissingReferenceException;
import ca.frar.jjjrmi.testclasses.ArrayWrapper;
import ca.frar.jjjrmi.testclasses.Has;
import ca.frar.jjjrmi.testclasses.HasHandler;
import ca.frar.jjjrmi.testclasses.MissingConstructor;
import ca.frar.jjjrmi.testclasses.Primitives;
import ca.frar.jjjrmi.testclasses.PrimitivesExtended;
import ca.frar.jjjrmi.testclasses.Simple;
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
        TranslatorResult encoded = translator.encode(object);
        Object decoded = translator.decode(encoded.toString()).getRoot();

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
        TranslatorResult encoded = translator.encode(object);
        Object decoded = translator.decode(encoded.toString()).getRoot();
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
        TranslatorResult encoded = translator1.encode(object);
        Object decoded = translator2.decode(encoded.toString()).getRoot();

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
        TranslatorResult encoded = translator.encode(arrayWrapper);
        ArrayWrapper decoded = (ArrayWrapper) translator.decode(encoded.toString()).getRoot();
        assertEquals(arrayWrapper.hashCode(), decoded.hashCode());
    }

    /*
     * Because the translator was cleared between encode/decode a new object is made.
     */
    @Test
    public void test_array_as_field_02() throws JJJRMIException {
        Translator translator = new Translator();
        ArrayWrapper arrayWrapper = new ArrayWrapper();
        TranslatorResult encoded = translator.encode(arrayWrapper);
        translator.clear();

        assertFalse(translator.hasReference("S0"));
        ArrayWrapper decoded = (ArrayWrapper) translator.decode(encoded.toString()).getRoot();
        assertNotEquals(arrayWrapper.hashCode(), decoded.hashCode());
        assertEquals(Arrays.toString(arrayWrapper.arrayField), Arrays.toString(decoded.arrayField));
    }

    @Test
    public void test_primitives() throws JJJRMIException {
        Translator translator = new Translator();
        Primitives object = new Primitives(9);
        TranslatorResult encoded = translator.encode(object);
        translator.clear();

        Primitives decoded = (Primitives) translator.decode(encoded.toString()).getRoot();
        assertTrue(object.equals(decoded));
    }

    @Test
    public void test_primitives_from_super() throws JJJRMIException {
        Translator translator = new Translator();
        PrimitivesExtended object = new PrimitivesExtended(9);
        TranslatorResult encoded = translator.encode(object);
        translator.clear();

        Primitives decoded = (PrimitivesExtended) translator.decode(encoded.toString()).getRoot();
        assertTrue(object.equals(decoded));
    }

    @Test
    public void test_missing_constructor() throws JJJRMIException {
        assertThrows(MissingConstructorException.class, () -> {
            Translator translator = new Translator();
            MissingConstructor object = new MissingConstructor(5);
            TranslatorResult encoded = translator.encode(object);
            translator.clear();
            translator.decode(encoded.toString());
        });
    }

    @Test
    public void test_null() throws JJJRMIException {
        Translator translator = new Translator();
        Has<Object> object = new Has<>(null);
        TranslatorResult encoded = translator.encode(object);
        translator.clear();

        Has<Object> decoded = (Has<Object>) translator.decode(encoded.toString()).getRoot();
        assertEquals(null, decoded.get());
    }
    
    @Test
    public void test_reference_as_root() throws JJJRMIException {
        Translator translator = new Translator();
        Has<Object> object = new Has<>(null);
        translator.encode(object);
        TranslatorResult encoded = translator.encode(object);
        Object decoded = translator.decode(encoded.toString()).getRoot();
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
        TranslatorResult encoded = translator.encode(has2); 
        translator.removeByValue(has2);
        Has<Simple> decoded = (Has<Simple>) translator.decode(encoded.toString()).getRoot();
        assertEquals(has1.get(), decoded.get());
    }    
        
    @Test
    public void test_array_of_empty() throws JJJRMIException {
        Translator translator = new Translator();
        boolean[] array = new boolean[0];
        Has<boolean[]> object = new Has<>(array);
        TranslatorResult encoded = translator.encode(object);
        translator.clear();
        Has<Object[]> decoded = (Has<Object[]>) translator.decode(encoded.toString()).getRoot();
        assertEquals(0, decoded.get().length);
    }
   
    @Test
    public void test_string_generic() throws JJJRMIException {
        Translator translator = new Translator();
        Has<String> object = new Has<>("i am string");
        TranslatorResult encoded = translator.encode(object);
        translator.clear();
        Has<String> decoded = (Has<String>) translator.decode(encoded.toString()).getRoot();
        assertEquals("i am string", decoded.get());
    }   
    
    @Test
    public void test_int_generic() throws JJJRMIException {
        Translator translator = new Translator();
        Has<Integer> object = new Has<>(5);
        TranslatorResult encoded = translator.encode(object);
        translator.clear();
        Has<Integer> decoded = (Has<Integer>) translator.decode(encoded.toString()).getRoot();
        assertTrue(5 == decoded.get());
    }       
    
    @Test
    public void test_bool_generic() throws JJJRMIException {
        Translator translator = new Translator();
        Has<Boolean> object = new Has<>(true);
        TranslatorResult encoded = translator.encode(object);
        translator.clear();
        Has<Boolean> decoded = (Has<Boolean>) translator.decode(encoded.toString()).getRoot();
        assertTrue(decoded.get());
    }  
    
    @Test
    public void test_double_generic() throws JJJRMIException {
        Translator translator = new Translator();
        Has<Double> object = new Has<>(5.1);
        TranslatorResult encoded = translator.encode(object);
        translator.clear();
        Has<Double> decoded = (Has<Double>) translator.decode(encoded.toString()).getRoot();
        assertTrue(5.1 == decoded.get());
    }          
    
    /**
     * Has handler z = x * y, after decode z = x + y;
     * @throws JJJRMIException 
     */
    @Test
    public void test_handler() throws JJJRMIException {
        Translator translator = new Translator();
        HasHandler object = new HasHandler(2, 5);
        TranslatorResult encoded = translator.encode(object);
        translator.clear();
        HasHandler decoded = (HasHandler) translator.decode(encoded.toString()).getRoot();
        assertEquals(7, decoded.z);
    }         

}
