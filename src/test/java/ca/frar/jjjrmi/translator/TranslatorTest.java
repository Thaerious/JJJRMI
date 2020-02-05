package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.exceptions.MissingHandlerException;
import ca.frar.jjjrmi.exceptions.NewHandlerException;
import ca.frar.jjjrmi.exceptions.SeekHandlersException;
import ca.frar.jjjrmi.exceptions.TranslatorException;
import ca.frar.jjjrmi.testableclasses.ForcedDeferred;
import ca.frar.jjjrmi.testableclasses.NoRetain;
import ca.frar.jjjrmi.testableclasses.Simple;
import ca.frar.jjjrmi.testable.handlers.ArrayListHandler;
import ca.frar.jjjrmi.testable.handlers.WrongConstructorHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import org.json.JSONObject;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * @author edward
 */
public class TranslatorTest {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JJJRMI");

    /**
     * Test of addReference method, of class Translator.
     */
    @Test
    public void testRemoveByValue_returnTrue() {
        String reference = "T0";
        Object object = new Simple();
        Translator instance = new Translator();
        instance.addReference(reference, object);
        assertTrue(instance.removeByValue(object));
    }

    /**
     * Test of addReference method, of class Translator.
     */
    @Test
    public void testRemoveByValue_returnFalse() {
        Object object = new Simple();
        Translator instance = new Translator();
        assertFalse(instance.removeByValue(object));
    }

    /**
     * Test of addReference method, of class Translator.
     */
    @Test
    public void testAddReference_hasReference() {
        String reference = "T0";
        Object object = new Simple();
        Translator instance = new Translator();
        instance.addReference(reference, object);
        assertTrue(instance.hasReference("T0"));
    }

    @Test
    public void testAddReference_hasReferredObject() {
        String reference = "T0";
        Object object = new Simple();
        Translator instance = new Translator();
        instance.addReference(reference, object);
        assertTrue(instance.hasReferredObject(object));
    }

    @Test
    public void testAddReference_getReferredObject() {
        String reference = "T0";
        Object object = new Simple();
        Translator instance = new Translator();
        instance.addReference(reference, object);
        assertEquals(object, instance.getReferredObject("T0"));
    }

    @Test
    public void testAddTempReference_getReferredObject() {
        String reference = "T0";
        Object object = new Simple();
        Translator instance = new Translator();
        instance.addTempReference(reference, object);
        assertEquals(object, instance.getReferredObject("T0"));
    }

    @Test
    public void testAddTempReference_hasReferredObject() {
        String reference = "T0";
        Object object = new Simple();
        Translator instance = new Translator();
        instance.addTempReference(reference, object);
        assertTrue(instance.hasReferredObject(object));
    }

    /**
     * Test of addReference method, of class Translator.
     */
    @Test
    public void testAddReference_notHasReference() {
        String reference = "T0";
        Object object = new Simple();
        Translator instance = new Translator();
        instance.addTempReference(reference, object);
        assertFalse(instance.hasReference("T1"));
    }

    @Test
    public void testAddReference_getReference() {
        String reference = "T0";
        Object object = new Simple();
        Translator instance = new Translator();
        instance.addReference(reference, object);
        assertEquals("T0", instance.getReference(object));
    }

    /**
     * Test of addTempReference method, of class Translator.
     */
    @Test
    public void testAddTempReference_hasReference() {
        String reference = "T0";
        Object object = new Simple();
        Translator instance = new Translator();
        instance.addTempReference(reference, object);
        assertTrue(instance.hasReference("T0"));
    }

    @Test
    public void testAddTempReference_getReference() {
        String reference = "T0";
        Object object = new Simple();
        Translator instance = new Translator();
        instance.addTempReference(reference, object);
        assertEquals("T0", instance.getReference(object));
    }

    /**
     * Test of addReference method, of class Translator.
     */
    @Test
    public void testAddTempReference_notHasReference() {
        String reference = "T0";
        Object object = new Simple();
        Translator instance = new Translator();
        instance.addTempReference(reference, object);
        assertFalse(instance.hasReference("T1"));
    }

    /**
     * When cleared no temp references remain.
     */
    @Test
    public void testClearTempReferences() {
        String reference = "T0";
        Object object = new Simple();
        Translator instance = new Translator();
        instance.addTempReference(reference, object);
        instance.clearTempReferences();
        assertFalse(instance.hasReference("T0"));
    }

    @Test
    public void testGetAllReferredObjects_00() {
        Translator instance = new Translator();
        Collection<Object> result = instance.getAllReferredObjects();
        assertEquals(0, result.size());
    }

    /**
     * Add single reference
     */
    @Test
    public void testGetAllReferredObjects_01() {
        Translator instance = new Translator();
        instance.addReference("T0", new Simple());
        Collection<Object> result = instance.getAllReferredObjects();
        assertEquals(1, result.size());
    }

    /**
     * Add single temp reference
     */
    @Test
    public void testGetAllReferredObjects_02() {
        Translator instance = new Translator();
        instance.addTempReference("T0", new Simple());
        Collection<Object> result = instance.getAllReferredObjects();
        assertEquals(1, result.size());
    }

    /**
     * Add single temp reference, after clear
     */
    @Test
    public void testGetAllReferredObjects_03() {
        Translator instance = new Translator();
        instance.addTempReference("T0", new Simple());
        instance.clearTempReferences();
        Collection<Object> result = instance.getAllReferredObjects();
        assertEquals(0, result.size());
    }

    /**
     * Add two references
     */
    @Test
    public void testGetAllReferredObjects_04() {
        Translator instance = new Translator();
        instance.addReference("T0", new Simple());
        instance.addTempReference("T1", new Simple());
        Collection<Object> result = instance.getAllReferredObjects();
        assertEquals(2, result.size());
    }

    /**
     * Add two references, one temp, after clear
     */
    @Test
    public void testGetAllReferredObjects_05() {
        Translator instance = new Translator();
        instance.addReference("T0", new Simple());
        instance.addTempReference("T1", new Simple());
        instance.clearTempReferences();
        Collection<Object> result = instance.getAllReferredObjects();
        assertEquals(1, result.size());
    }

    /**
     * Test of clear method, of class Translator.
     */
    @Test
    public void testClear_00() {
        Translator instance = new Translator();
        instance.addReference("T0", new Simple());
        instance.addTempReference("T1", new Simple());
        instance.clear();
        Collection<Object> result = instance.getAllReferredObjects();
        assertEquals(0, result.size());
    }

    @Test
    public void testEncode_hasRefObj() throws JJJRMIException {
        Translator instance = new Translator();
        Simple simple = new Simple();
        instance.encode(simple);
        assertTrue(instance.hasReferredObject(simple));
    }

    @Test
    public void testEncode_hasRef() throws JJJRMIException {
        Translator instance = new Translator();
        Simple simple = new Simple();
        instance.encode(simple);
        assertTrue(instance.hasReference("S0"));
    }

    @Test
    public void testEncode_getRef() throws JJJRMIException {
        Translator instance = new Translator();
        Simple simple = new Simple();
        instance.encode(simple);
        assertEquals("S0", instance.getReference(simple));
    }

    @Test
    public void testEncode_getRef_2() throws JJJRMIException {
        Translator instance = new Translator();
        instance.encode(new Simple());
        Simple simple = new Simple();
        instance.encode(simple);
        assertEquals("S1", instance.getReference(simple));
    }

    @Test
    public void testEncode_notRetain() throws JJJRMIException {
        Translator instance = new Translator();
        NoRetain noRetain = new NoRetain();
        instance.encode(noRetain);
        assertFalse(instance.hasReferredObject(noRetain));
    }

    /**
     * Test known class JS constructed. An object may not have been decoded when
     * it's reference is first encountered. It should defer dereferencing the
     * pointer until the object is decoded.
     *
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws EncoderException
     * @throws DecoderException
     * @throws IOException
     */
    @Test
    public void test_self_deferred() throws JJJRMIException, IOException {
        Translator translator = new Translator();

        String jsonString
                = "{\n"
                + "  'retain': true,\n"
                + "  'type': 'ca.frar.jjjrmi.testable.ForcedDeferred',\n"
                + "  'fields': {\n"
                + "    'hasInt2': {'ptr': 'S1'},\n"
                + "    'hasInt1': {\n"
                + "      'retain': true,\n"
                + "      'type': 'ca.frar.jjjrmi.testable.HasInt',\n"
                + "      'fields': {'x': {\n"
                + "        'primitive': 'number',\n"
                + "        'value': 0\n"
                + "      }},\n"
                + "      'key': 'S1'\n"
                + "    }\n"
                + "  },\n"
                + "  'key': 'S0'\n"
                + "}";

        Object decode = translator.decode(jsonString);
        assertEquals(ForcedDeferred.class, decode.getClass());
    }

    /**
     * Test setting a single handler by class. After being added a handler's
     * existence can be determined by using the handled class as a key. After
     * being added a handler can be retrieved by using the handled class as a
     * key.
     */
    @Test
    public void test_add_handler() throws JJJRMIException {
        Translator translator = new Translator();

        translator.setHandler(java.util.ArrayList.class, ArrayListHandler.class);

        /* After being added a handler's existence can be determined by using the handled class as a key. */
        assertTrue(translator.hasHandler(java.util.ArrayList.class));

        /* After being added a handler can be retrieved by using the handled class as a key. */
        assertEquals(ArrayListHandler.class, translator.newHandler(java.util.ArrayList.class, new JSONObject()).getClass());
    }

    /**
     * Test using a handler. The encoder will add a handler record to the json.
     */
    @Test
    public void test_use_handler_encode() throws JJJRMIException {
        Translator translator = new Translator();
        translator.setHandler(java.util.ArrayList.class, ArrayListHandler.class);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("ONE");
        arrayList.add("TWO");
        JSONObject encode = translator.encode(arrayList);

        assertEquals("ca.frar.jjjrmi.testable.handlers.ArrayListHandler", encode.get(Constants.HandlerParam));
    }

    /**
     * Test using a handler. The encoder will add a handler record to the json.
     */
    @Test
    public void test_use_handler_decode() throws JJJRMIException {
//        Translator translator = new Translator();
//        translator.setHandler(java.util.ArrayList.class, ArrayListHandler.class);
//        ArrayList<String> arrayList = new ArrayList<>();
//        arrayList.add("ONE");
//        arrayList.add("TWO");
//        JSONObject encode = translator.encode(arrayList);
//
//        translator.clear();
//        ArrayList<String> decode = (ArrayList<String>) translator.decode(encode);
//
//        assertEquals(ArrayList.class, decode.getClass());
//        assertEquals("ONE", decode.get(0));
//        assertEquals("TWO", decode.get(1));
//        assertEquals(2, decode.size());
    }

    /**
     * Automatically search the classpath for handlers and set them. Uses the
     * @Handles annotation to set to the target class. There are two in the test
     * classes.
     */
    @Test
    public void test_seek_handlers() {
        Translator translator = new Translator();
        
        try {
            translator.seekHandlers();
        } catch (TranslatorException ex) {
            /* ignore, see test_seek_handlers_unknown_class() */
        }

        assertTrue(translator.hasHandler(ArrayList.class));
        assertTrue(translator.hasHandler(HashMap.class));
    }

    /**
     * A handler must has a (Translator, JSONObject) constructor.
     * note: it doesn't have @Handles so that it isn't picked up by seekHandlers
     */
    @Test
    public void test_add_handler_wrong_constructor() throws JJJRMIException {
        Translator translator = new Translator();
        
        assertThrows(NewHandlerException.class, () -> {
            translator.setHandler(ArrayList.class, WrongConstructorHandler.class);
            translator.newHandler(ArrayList.class, new JSONObject());
        });
    }
    
    /**
     * Attempting to retrieve a handler for an unregistered class will throw an
     * exception.
     */
    @Test
    public void test_add_no_handler_constructor() throws JJJRMIException {
        Translator translator = new Translator();
        
        assertThrows(MissingHandlerException.class, () -> {
            translator.newHandler(ArrayList.class, new JSONObject());
        });
    }    
    
    /**
     * If the class in @Handles is not found it will throw an exception.
     */
    @Test
    public void test_seek_handlers_unknown_class() throws JJJRMIException {
        Translator translator = new Translator();
        
        assertThrows(SeekHandlersException.class, () -> {
            translator.seekHandlers();
        });
    }       
}
