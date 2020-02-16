/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.translator;

import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.testclasses.ArrayWrapper;
import org.junit.jupiter.api.Test;
import ca.frar.jjjrmi.testclasses.Simple;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test the operation of the encoder and decoder in tandum. This test runs as
 * much of the code as possible to ensure there are no catastrophic errors.
 *
 * @author Ed Armstrong
 */
public class TranslatorClearTest {

    @Test
    public void test_clear_with_one() throws EncoderException, DecoderException {
        Translator translator = new Translator();
        Simple object = new Simple();
        translator.encode(object);
        translator.clear();
        assertEquals(0, translator.size());
        assertFalse(translator.hasReferredObject(object));
    }

    @Test
    public void test_clear_empty() throws EncoderException, DecoderException {
        Translator translator = new Translator();
        translator.clear();
        assertEquals(0, translator.size());
    }

    @Test
    public void test_clear_array() throws EncoderException, DecoderException {
        Translator translator = new Translator();
        ArrayWrapper arrayWrapper = new ArrayWrapper();
        translator.encode(arrayWrapper);
        translator.clear();
        assertEquals(0, translator.size());
    }
    
    @Test
    public void test_clear_then_decode() throws EncoderException, DecoderException {
        Translator translator = new Translator();
        ArrayWrapper arrayWrapper = new ArrayWrapper();
        TranslatorResult encoded = translator.encode(arrayWrapper);
        translator.clear();
        Object decode = translator.decode(encoded.toString());
        assertEquals(arrayWrapper.getClass(), decode.getClass());
    }    
}
