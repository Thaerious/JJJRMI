/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.exceptions.RootException;
import ca.frar.jjjrmi.translator.testclasses.Has;
import ca.frar.jjjrmi.translator.testclasses.HasHandler;
import ca.frar.jjjrmi.translator.testclasses.Primitives;
import ca.frar.jjjrmi.translator.testclasses.Simple;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Make sure that the encoder doesn't fail catastrophically.  Checks the
 * root-object and the size of the resulting new-objects.  The encoding of the
 * JSON encoded objects is not checked.
 * @author Ed Armstrong
 */
public class EncoderSanityTest {

    /**
     * A single object in a new empty translator.
     */
    @Test
    public void test_simple_class() throws JJJRMIException{
        Translator translator = new Translator();
        Simple object = new Simple();
        TranslatorResult result = translator.encode(object);
        assertEquals(1, result.newObjectCount());
    }

    /**
     * Same class encoded twice doesn't return a new object.
     * It will have the same reference both times.
     */
    @Test
    public void test_simple_class_same() throws JJJRMIException{
        Translator translator = new Translator();
        Simple object = new Simple();
        TranslatorResult result1 = translator.encode(object);
        TranslatorResult result2 = translator.encode(object);

        assertEquals(0, result2.newObjectCount());
        assertEquals(result1.getRoot(), result2.getRoot());
    }

    @Test
    public void test_has_null_field() throws JJJRMIException{
        Translator translator = new Translator();
        Has<Object> object = new Has<>(null);
        TranslatorResult result = translator.encode(object);
        assertEquals(1, result.newObjectCount());
    }

    @Test
    public void test_all_primatives() throws JJJRMIException{
        Translator translator = new Translator();
        Primitives object = new Primitives();
        TranslatorResult result = translator.encode(object);
        assertEquals(1, result.newObjectCount());
    }

    /**
     * The root object must be a non-null object.
     */
    @Test
    public void test_root_is_null_throws_exception() throws JJJRMIException{
        Translator translator = new Translator();
        Object object = null;

        assertThrows(RootException.class, () -> {
            TranslatorResult result = translator.encode(object);
        });
    }

    /**
     * An object references a previously encoded object.
     * There will only be one new object.
     * @throws JJJRMIException
     */
    @Test
    public void test_previously_encoded() throws JJJRMIException{
        Translator translator = new Translator();
        Simple simple = new Simple();
        Has<Simple> has = new Has<>(simple);
        translator.encode(simple);
        TranslatorResult result = translator.encode(has);
        assertEquals(1, result.newObjectCount());
    }

    /**
     * Encoded object has object not previously encoded.
     * Both will be new objects.
     * @throws JJJRMIException
     */
    @Test
    public void test_not_previously_encoded() throws JJJRMIException{
        Translator translator = new Translator();
        Simple simple = new Simple();
        Has<Simple> has = new Has<>(simple);
        TranslatorResult result = translator.encode(has);
        assertEquals(2, result.newObjectCount());
    }

    /**
     * Encoded object contains an array of primitives.
     * @throws JJJRMIException
     */
    @Test
    public void test_array_of_int() throws JJJRMIException{
        Translator translator = new Translator();
        int[] array = new int[3];
        Has<int[]> has = new Has<>(array);
        TranslatorResult result = translator.encode(has);
        assertEquals(1, result.newObjectCount());
    }
    
    /**
     * Encode a root object which has a handler.
     * @throws JJJRMIException
     */
    @Test
    public void test_handler() throws JJJRMIException{
        Translator translator = new Translator();
        TranslatorResult result = translator.encode(new HasHandler(1, 2.3f));
        assertEquals(1, result.newObjectCount());
    }    
    
    /**
     * Encode non-root object which has a handler.
     * @throws JJJRMIException
     */
    @Test
    public void test_handler_as_field() throws JJJRMIException{
        Translator translator = new Translator();
        HasHandler hasHandler = new HasHandler(1, 2.3f);
        Has<HasHandler> has = new Has<HasHandler>(hasHandler);
        TranslatorResult result = translator.encode(has);
        assertEquals(2, result.newObjectCount());
    }        
}
