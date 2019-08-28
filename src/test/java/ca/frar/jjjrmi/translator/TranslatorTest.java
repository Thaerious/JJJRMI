package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.test.testable.NoRetain;
import ca.frar.jjjrmi.test.testable.Simple;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author edward
 */
public class TranslatorTest {
    
    @Test
    public void testDecode(){
    }
    
    /**
     * Test of addReference method, of class Translator.
     */
    @Test
    public void testRemoveByValue_returnTrue() {
        System.out.println("removeByValue");
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
        System.out.println("removeByValue");
        Object object = new Simple();
        Translator instance = new Translator();
        assertFalse(instance.removeByValue(object));
    }
    
    /**
     * Test of addReference method, of class Translator.
     */
    @Test
    public void testAddReference_hasReference() {
        System.out.println("addReference");
        String reference = "T0";
        Object object = new Simple();
        Translator instance = new Translator();
        instance.addReference(reference, object);
        assertTrue(instance.hasReference("T0"));
    }

    @Test
    public void testAddReference_hasReferredObject() {
        System.out.println("addReference");
        String reference = "T0";
        Object object = new Simple();
        Translator instance = new Translator();
        instance.addReference(reference, object);
        assertTrue(instance.hasReferredObject(object));
    }

    @Test
    public void testAddReference_getReferredObject() {
        System.out.println("addReference");
        String reference = "T0";
        Object object = new Simple();
        Translator instance = new Translator();
        instance.addReference(reference, object);
        assertEquals(object, instance.getReferredObject("T0"));
    }

    @Test
    public void testAddTempReference_getReferredObject() {
        System.out.println("addReference");
        String reference = "T0";
        Object object = new Simple();
        Translator instance = new Translator();
        instance.addTempReference(reference, object);
        assertEquals(object, instance.getReferredObject("T0"));
    }

    @Test
    public void testAddTempReference_hasReferredObject() {
        System.out.println("addReference");
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
        System.out.println("addReference");
        String reference = "T0";
        Object object = new Simple();
        Translator instance = new Translator();
        instance.addTempReference(reference, object);
        assertFalse(instance.hasReference("T1"));
    }

    @Test
    public void testAddReference_getReference() {
        System.out.println("addReference");
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
        System.out.println("addReference");
        String reference = "T0";
        Object object = new Simple();
        Translator instance = new Translator();
        instance.addTempReference(reference, object);
        assertTrue(instance.hasReference("T0"));
    }

    @Test
    public void testAddTempReference_getReference() {
        System.out.println("addReference");
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
        System.out.println("addReference");
        String reference = "T0";
        Object object = new Simple();
        Translator instance = new Translator();
        instance.addTempReference(reference, object);
        assertFalse(instance.hasReference("T1"));
    }

    /**
     * Test of addReference method, of class Translator.
     */
    @Test
    public void testClearTempReferences() {
        System.out.println("addReference");
        String reference = "T0";
        Object object = new Simple();
        Translator instance = new Translator();
        instance.addTempReference(reference, object);
        instance.clearTempReferences();
        assertFalse(instance.hasReference("T0"));
    }

    @Test
    public void testGetAllReferredObjects_00() {
        System.out.println("getAllReferredObjects");
        Translator instance = new Translator();
        Collection<Object> result = instance.getAllReferredObjects();
        assertEquals(0, result.size());
    }

    /**
     * Add single reference
     */
    @Test
    public void testGetAllReferredObjects_01() {
        System.out.println("getAllReferredObjects");
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
        System.out.println("getAllReferredObjects");
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
        System.out.println("getAllReferredObjects");
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
        System.out.println("getAllReferredObjects");
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
        System.out.println("getAllReferredObjects");
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
        System.out.println("clear");
        Translator instance = new Translator();
        instance.addReference("T0", new Simple());
        instance.addTempReference("T1", new Simple());
        instance.clear();
        Collection<Object> result = instance.getAllReferredObjects();
        assertEquals(0, result.size());
    }
    
    /**
     * Test of clear method, of class Translator.
     * Doesn't reset allocNextKey
     */
    @Test
    public void testClear_01() {
        System.out.println("clear");
        Translator instance = new Translator();
        instance.addReference("T0", new Simple());
        instance.addTempReference("T1", new Simple());
        String allocNextKey = instance.allocNextKey();
        instance.clear();
        Collection<Object> result = instance.getAllReferredObjects();
        assertEquals("S1", instance.allocNextKey());
    }
    
    @Test
    public void testAllocNextKey_First() {
        System.out.println("allocNextKey");
        Translator instance = new Translator();
        assertEquals("S0", instance.allocNextKey());
    }    

    @Test
    public void testAllocNextKey_Second() {
        System.out.println("allocNextKey");
        Translator instance = new Translator();
        instance.allocNextKey();
        assertEquals("S1", instance.allocNextKey());
    }    
    
    @Test
    public void testAllocNextKey_Fifth() {
        System.out.println("allocNextKey");
        Translator instance = new Translator();
        for (int i = 0; i < 4; i++) instance.allocNextKey();
        assertEquals("S4", instance.allocNextKey());
    }        
   
    /**
     * next key increments
     */
    @Test
    public void testEncode_nextKeyInc() throws IllegalArgumentException, IllegalAccessException, EncoderException {
        System.out.println("encode");
        Translator instance = new Translator();
        instance.encode(new Simple());        
        assertEquals("S1", instance.allocNextKey());
    }    
    
    @Test
    public void testEncode_hasRefObj() throws IllegalArgumentException, IllegalAccessException, EncoderException {
        System.out.println("encode");
        Translator instance = new Translator();
        Simple simple = new Simple();
        instance.encode(simple);        
        assertTrue(instance.hasReferredObject(simple));
    }      
    
    @Test
    public void testEncode_hasRef() throws IllegalArgumentException, IllegalAccessException, EncoderException {
        System.out.println("encode");
        Translator instance = new Translator();
        Simple simple = new Simple();
        instance.encode(simple);        
        assertTrue(instance.hasReference("S0"));
    }          
    
    @Test
    public void testEncode_getRef() throws IllegalArgumentException, IllegalAccessException, EncoderException {
        System.out.println("encode");
        Translator instance = new Translator();
        Simple simple = new Simple();
        instance.encode(simple);        
        assertEquals("S0", instance.getReference(simple));
    }        
    
    @Test
    public void testEncode_getRef_2() throws IllegalArgumentException, IllegalAccessException, EncoderException {
        System.out.println("encode");
        Translator instance = new Translator();
        instance.encode(new Simple());        
        Simple simple = new Simple();
        instance.encode(simple);        
        assertEquals("S1", instance.getReference(simple));
    }
    
    @Test
    public void testEncode_notRetain() throws IllegalArgumentException, IllegalAccessException, EncoderException {
        System.out.println("encode");
        Translator instance = new Translator();
        NoRetain noRetain = new NoRetain();
        instance.encode(noRetain);        
        assertFalse(instance.hasReferredObject(noRetain));
    }    
}
