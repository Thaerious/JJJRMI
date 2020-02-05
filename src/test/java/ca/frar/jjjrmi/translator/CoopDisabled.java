package ca.frar.jjjrmi.translator;

import ca.frar.jjjrm.test.jsportal.JSExec;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.testableclasses.Foo;
import ca.frar.jjjrmi.testableclasses.HasInt;
import ca.frar.jjjrmi.testableclasses.Primitives;
import ca.frar.jjjrmi.testableclasses.SelfReferential;
import java.io.IOException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Java-JS co-operation tests.
 * @author edward
 */
public class CoopDisabled {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JJJRMI");

    @Test
    public void test_primative() throws JJJRMIException, IOException {
        Translator translator = new Translator();
        JSONObject encodedJava = translator.encode(1).getAllObjects().get(0);
        JSExec jsExec = new JSExec();
        jsExec.start("src/test/js/Translator.js");
        jsExec.writeLine(encodedJava.toString());
        String encodedJS = jsExec.stop();
        Object decoded = translator.decode(encodedJS);
                
        assertEquals(decoded, 1);
    }

    /**
     * Test Unknown Class.
     * An unknown class has not been registered with the Javascript translator.
     * It can still be sent from the server (Java) to the client (JS), and will
     * by default be retained.  It can not be instantiated client side as the
     * object must have a known type, though it can be returned.
     */
    @Test
    public void test_unknown_class() throws JJJRMIException, IOException {
        Translator translator = new Translator();
        
        HasInt hasInt = new HasInt();
        JSONObject encodedJava = translator.encode(hasInt).getAllObjects().get(0);
        JSExec jsExec = new JSExec();
        jsExec.start("src/test/js/Translator.js");
        jsExec.writeLine(encodedJava.toString());
        String encodedJS = jsExec.stop();
        Object decoded = translator.decode(encodedJS);
        
        /* same objects */
        assertEquals(decoded, hasInt);
    }  
    
    /**
     * Test known class JS constructed.
     * Decode a class that was instantiated by the JS translator.
     * Client creates a Foo class, sends it to Server.
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws EncoderException
     * @throws DecoderException
     * @throws IOException 
     */
    @Test
    public void test_known_class_js_contructed() throws JJJRMIException, IOException {
        Translator translator = new Translator();
        
        JSExec jsExec = new JSExec();
        jsExec.start("src/test/js/GenerateFoo.js");
        String encodedJS = jsExec.stop();
        Object decoded = translator.decode(encodedJS);       
        
        assertEquals(Foo.class, decoded.getClass());
    }     
    
    /**
     * Test known class JS constructed.
     * Foo is not transient, the client will return a reference to the object
     * sent.
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws EncoderException
     * @throws DecoderException
     * @throws IOException 
     */
    @Test
    public void test_known_class_java_contructed() throws JJJRMIException, IOException {
        Translator translator = new Translator();
        
        Foo foo = new Foo(8);
        JSONObject encodedJava = translator.encode(foo).getAllObjects().get(0);
        
        JSExec jsExec = new JSExec();
        jsExec.start("src/test/js/Translator.js");
        
        jsExec.writeLine(encodedJava.toString());
        String encodedJS = jsExec.stop();
        Object decoded = translator.decode(encodedJS);
        
        /* same objects */
        assertEquals(decoded, foo);
    }    
    
    /**
     * Test known class JS constructed.
     * Foo is not transient, the client will return a reference to the object
     * sent.
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws EncoderException
     * @throws DecoderException
     * @throws IOException 
     */
    @Test
    public void test_self_referential() throws JJJRMIException, IOException {
        Translator translator = new Translator();
        
        SelfReferential selfReferential = new SelfReferential();
        JSONObject encodedJava = translator.encode(selfReferential);
        
        JSExec jsExec = new JSExec();
        jsExec.start("src/test/js/Translator.js");
        
        jsExec.writeLine(encodedJava.toString());
        String encodedJS = jsExec.stop();
        Object decoded = translator.decode(encodedJS);        
        
        /* same objects */
        assertEquals(decoded, selfReferential);
    }              
    
/**
     * Sanity check primitives.
     * Primitives is a transient object.
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws EncoderException
     * @throws DecoderException
     * @throws IOException 
     */
    @Test
    public void test_primitives() throws JJJRMIException, IOException {
        Translator translator = new Translator();
        
        Primitives primitives = new Primitives();
        JSONObject encodedJava = translator.encode(primitives);
        
        JSExec jsExec = new JSExec();
        jsExec.start("src/test/js/Translator.js");
        
        jsExec.writeLine(encodedJava.toString());
        String encodedJS = jsExec.stop();
        Object decoded = translator.decode(encodedJS);        
        
        /* same objects */
        assertTrue(primitives.equals(decoded));
    }                  
    
}
