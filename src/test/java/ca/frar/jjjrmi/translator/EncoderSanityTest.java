/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.translator;

import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.exceptions.RootException;
import ca.frar.jjjrmi.testclasses.Has;
import ca.frar.jjjrmi.testclasses.HasHandler;
import ca.frar.jjjrmi.testclasses.Primitives;
import ca.frar.jjjrmi.testclasses.Simple;
import ca.frar.jjjrmi.translator.encoder.EncodedResult;
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
     * @throws EncoderException
     */
    @Test
    public void test_simple_class() throws EncoderException{
        Translator translator = new Translator();
        Simple object = new Simple();
        EncodedResult result = translator.encode(object);
        assertEquals(1, result.getAllObjects().size());
        assertTrue(result.has(Constants.RootObject));
    }

    /**
     * Same class encoded twice doesn't return a new object.
     * It will have the same reference both times.
     * @throws EncoderException
     */
    @Test
    public void test_simple_class_same() throws EncoderException{
        Translator translator = new Translator();
        Simple object = new Simple();
        EncodedResult result1 = translator.encode(object);
        EncodedResult result2 = translator.encode(object);

        assertEquals(0, result2.getAllObjects().size());
        assertEquals(result1.getString(Constants.RootObject), result2.getString(Constants.RootObject));
        assertTrue(result1.has(Constants.RootObject));
    }

    @Test
    public void test_has_null_field() throws EncoderException{
        Translator translator = new Translator();
        Has<Object> object = new Has<>(null);
        EncodedResult result = translator.encode(object);
        assertEquals(1, result.getAllObjects().size());
        assertTrue(result.has(Constants.RootObject));
    }

    @Test
    public void test_all_primatives() throws EncoderException{
        Translator translator = new Translator();
        Primitives object = new Primitives();
        EncodedResult result = translator.encode(object);
        assertEquals(1, result.getAllObjects().size());
        assertTrue(result.has(Constants.RootObject));
    }

    /**
     * The root object must be a non-null object.
     * @throws EncoderException
     */
    @Test
    public void test_root_is_null_throws_exception() throws EncoderException{
        Translator translator = new Translator();
        Object object = null;

        assertThrows(RootException.class, () -> {
            EncodedResult result = translator.encode(object);
        });
    }

    /**
     * An object references a previously encoded object.
     * There will only be one new object.
     * @throws EncoderException
     */
    @Test
    public void test_previously_encoded() throws EncoderException{
        Translator translator = new Translator();
        Simple simple = new Simple();
        Has<Simple> has = new Has<>(simple);
        translator.encode(simple);
        EncodedResult result = translator.encode(has);
        assertEquals(1, result.getAllObjects().size());
        assertTrue(result.has(Constants.RootObject));
    }

    /**
     * Encoded object has object not previously encoded.
     * Both will be new objects.
     * @throws EncoderException
     */
    @Test
    public void test_not_previously_encoded() throws EncoderException{
        Translator translator = new Translator();
        Simple simple = new Simple();
        Has<Simple> has = new Has<>(simple);
        EncodedResult result = translator.encode(has);
        assertEquals(2, result.getAllObjects().size());
        assertTrue(result.has(Constants.RootObject));
    }

    /**
     * Encoded object contains an array of primitives.
     * @throws EncoderException
     */
    @Test
    public void test_array_of_int() throws EncoderException{
        Translator translator = new Translator();
        int[] array = new int[3];
        Has<int[]> has = new Has<>(array);
        EncodedResult result = translator.encode(has);
        assertEquals(1, result.getAllObjects().size());
        assertTrue(result.has(Constants.RootObject));
    }
    
    /**
     * Encode a root object which has a handler.
     * @throws EncoderException
     */
    @Test
    public void test_handler() throws EncoderException{
        Translator translator = new Translator();
        EncodedResult result = translator.encode(new HasHandler(1, 2.3f));
        assertEquals(1, result.getAllObjects().size());
        assertTrue(result.has(Constants.RootObject));
    }    
    
    /**
     * Encode non-root object which has a handler.
     * @throws EncoderException
     */
    @Test
    public void test_handler_as_field() throws EncoderException{
        Translator translator = new Translator();
        HasHandler hasHandler = new HasHandler(1, 2.3f);
        Has<HasHandler> has = new Has<HasHandler>(hasHandler);
        EncodedResult result = translator.encode(has);
        assertEquals(2, result.getAllObjects().size());
        assertTrue(result.has(Constants.RootObject));
    }        
}
