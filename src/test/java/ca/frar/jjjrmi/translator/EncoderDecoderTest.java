/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.testableclasses.Has;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import ca.frar.jjjrmi.testableclasses.Simple;
import ca.frar.jjjrmi.translator.encoder.EncodedResult;

/**
 * Test the operation of the encoder and decoder in tandum.
 * @author Ed Armstrong
 */
public class EncoderDecoderTest {
    
    /**
     * The same translator can decode the json it created.
     * @throws EncoderException 
     */
    @Test
    public void test_simple() throws EncoderException, DecoderException{
        Translator translator = new Translator();
        Simple object = new Simple();
        EncodedResult encoded = translator.encode(object);
        Object decoded = translator.decode(encoded);
        
        assertEquals(Simple.class, decoded.getClass());
        assertEquals(object, decoded);
    }
    
    /**
     * Changing the local copies values will be maintained.
     * @throws EncoderException 
     */
    @Test
    public void test_simple_retain_values() throws EncoderException, DecoderException{
        Translator translator = new Translator();
        Has<Integer> object = new Has<>(3);
        EncodedResult encoded = translator.encode(object);
        Object decoded = translator.decode(encoded);
        object.set(7);
        
        assertEquals(Has.class, decoded.getClass());
        assertEquals(object, decoded);
        assertEquals(7, object.get().intValue());
    }    
}
