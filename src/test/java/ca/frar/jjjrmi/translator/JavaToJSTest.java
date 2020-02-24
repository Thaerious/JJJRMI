package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.testclasses.ArrayWrapper;
import ca.frar.jjjrmi.testclasses.Has;
import ca.frar.jjjrmi.testclasses.None;
import ca.frar.jjjrmi.testclasses.Primitives;
import ca.frar.jjjrmi.testclasses.Simple;
import static ca.frar.jjjrmi.translator.TranslatorClient.HOST;
import static ca.frar.jjjrmi.translator.TranslatorClient.PORT;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Ed Armstrong
 */
public class JavaToJSTest {
    
    @Test
    public void test_none() throws IOException, JJJRMIException{
        TranslatorClient tc = new TranslatorClient().connect(HOST, PORT);
        Translator translator = new Translator();
        TranslatorResult result = translator.encode(new None());
        String r = tc.cmd("decode", result.toString());
        
        tc.close();
        
        assertEquals("ca.frar.jjjrmi.testclasses.None", r);
    }
    
    @Test
    public void test_simple() throws IOException, JJJRMIException{
        TranslatorClient tc = new TranslatorClient().connect(HOST, PORT);
        Translator translator = new Translator();
        TranslatorResult result = translator.encode(new Simple());
        String r = tc.cmd("decode", result.toString());
        int sz = Integer.parseInt(tc.cmd("size"));
        boolean hasField = Boolean.parseBoolean(tc.cmd("hasField", "shape"));
        String getField = tc.cmd("getField", "shape");
        
        tc.close();
        
        assertEquals("ca.frar.jjjrmi.testclasses.Simple", r);
        assertEquals(1, sz);
        assertTrue(hasField);
        assertEquals("CIRCLE", getField);
    } 
    
    /**
     * Sending a reference to a previously cached object will invoke the object.
     * @throws EncoderException
     */
    @Test
    public void test_simple_retain_values() throws IOException, JJJRMIException{
        TranslatorClient tc = new TranslatorClient().connect(HOST, PORT);
        Translator translator = new Translator();
        Has<Integer> object = new Has<>(3);        
        TranslatorResult result = translator.encode(object);
        tc.cmd("decode", result.toString());
        tc.cmd("clearLast");
        String r = tc.cmd("decode", translator.encode(object).toString());
        
        tc.close();
        
        assertEquals("ca.frar.jjjrmi.testclasses.Has", r);
    }
    
    @Test
    public void test_array_field_00() throws IOException, JJJRMIException {
        TranslatorClient tc = new TranslatorClient().connect(HOST, PORT);
        Translator translator = new Translator();
        ArrayWrapper arrayWrapper = new ArrayWrapper();
        TranslatorResult encoded = translator.encode(arrayWrapper);
        
        tc.cmd("decode", encoded.toString());
        tc.cmd("pushField", "arrayField1");
        tc.cmd("getField", "0");
        
        assertEquals("1", tc.cmd("getField", "0"));
        assertEquals("5", tc.cmd("getField", "4"));
        
        tc.cmd("pop");
        tc.cmd("pushField", "arrayField3");
        String circleField = tc.cmd("getField", "0");
        
        tc.close();
        
        assertEquals("CIRCLE", circleField);
    }  
    
    /**
     * Getting the 'string' field will ensure that the values were passes 
     * and it wasn't just the constructor being called.
     * @throws JJJRMIException
     * @throws IOException 
     */
    @Test
    public void test_primitives() throws JJJRMIException, IOException {
        TranslatorClient tc = new TranslatorClient().connect(HOST, PORT);
        Translator translator = new Translator();
        Primitives object = new Primitives(9);
        TranslatorResult encoded = translator.encode(object);

        tc.cmd("decode", encoded.toString());
        String stringField = tc.cmd("getField", "string");
        
        tc.close();
        
        assertEquals("alpha9", stringField);
    }    
}