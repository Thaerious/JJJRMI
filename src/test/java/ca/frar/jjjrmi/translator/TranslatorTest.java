package ca.frar.jjjrmi.translator;

import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.exceptions.MissingConstructorException;
import ca.frar.jjjrmi.exceptions.UnknownReferenceException;
import ca.frar.jjjrmi.exceptions.UntrackedObjectException;
import ca.frar.jjjrmi.testclasses.ArrayWrapper;
import ca.frar.jjjrmi.testclasses.CustomHash;
import ca.frar.jjjrmi.testclasses.DoNotRetainAnno;
import ca.frar.jjjrmi.testclasses.DoNotRetainExtends;
import ca.frar.jjjrmi.testclasses.Has;
import ca.frar.jjjrmi.testclasses.HasHandler;
import ca.frar.jjjrmi.testclasses.MissingConstructor;
import ca.frar.jjjrmi.testclasses.None;
import ca.frar.jjjrmi.testclasses.Primitives;
import ca.frar.jjjrmi.testclasses.PrimitivesExtended;
import ca.frar.jjjrmi.testclasses.Simple;
import ca.frar.jjjrmi.testclasses.TransientField;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author edward
 */
public class TranslatorTest {

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

    @Test
    public void test_none() throws JJJRMIException {
        Translator translator = new Translator();
        None object = new None();
        TranslatorResult encoded = translator.encode(object);
        Object decoded = translator.decode(encoded.toString()).getRoot();

        assertEquals(None.class, decoded.getClass());
        assertEquals(object, decoded);
        assertEquals(1, translator.size());
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
        assertEquals(Arrays.toString(arrayWrapper.arrayField1), Arrays.toString(decoded.arrayField1));
        assertEquals(Arrays.toString(arrayWrapper.arrayField2), Arrays.toString(decoded.arrayField2));
        assertEquals(5, arrayWrapper.arrayField2.length);
        assertEquals(Arrays.toString(arrayWrapper.arrayField3), Arrays.toString(decoded.arrayField3));
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

    /**
     * The same object has been encoded twice. The second encoding sent returns
     * a reference to the first.
     *
     * @throws JJJRMIException
     */
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
     *
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
        translator.removeTrackedObject(has2);
        Has<Simple> decoded = (Has<Simple>) translator.decode(encoded.toString()).getRoot();
        assertEquals(has1.get(), decoded.get());
    }

    public void test_remove_untracked() {
        assertThrows(UntrackedObjectException.class, () -> {
            Translator translator = new Translator();
            Object obj = new None();
            translator.removeTrackedObject(obj);
        });
    }

    public void test_unknown_reference_0() {
        assertThrows(UnknownReferenceException.class, () -> {
            Translator translator = new Translator();
            translator.getReferredObject("X");
        });
    }

    public void test_unknown_reference_1() {
        assertThrows(UntrackedObjectException.class, () -> {
            Translator translator = new Translator();
            translator.getReference(new None());
        });
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
     *
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

    /**
     * Get all object on new translator, is empty.
     *
     * @throws JJJRMIException
     */
    @Test
    public void test_get_all_tracked_0() throws JJJRMIException {
        Translator translator = new Translator();
        Collection<Object> all = translator.getAllTrackedObjects();
        assertEquals(0, all.size());
    }

    /**
     * Get all object on simple tracked object.
     *
     * @throws JJJRMIException
     */
    @Test
    public void test_get_all_tracked_1() throws JJJRMIException {
        Translator translator = new Translator();
        translator.encode(new None());
        Collection<Object> all = translator.getAllTrackedObjects();
        assertEquals(1, all.size());
    }

    /**
     * Get all object on multiple tracked objects.
     *
     * @throws JJJRMIException
     */
    @Test
    public void test_get_all_tracked_2() throws JJJRMIException {
        Translator translator = new Translator();
        translator.encode(new None());
        translator.encode(new None());
        translator.encode(new None());
        Collection<Object> all = translator.getAllTrackedObjects();
        assertEquals(3, translator.size());
        assertEquals(3, all.size());
    }

    /**
     * Get all object after clear, is empty.
     *
     * @throws JJJRMIException
     */
    @Test
    public void test_get_all_tracked_3() throws JJJRMIException {
        Translator translator = new Translator();
        translator.encode(new None());
        translator.encode(new None());
        translator.encode(new None());
        translator.clear();
        Collection<Object> all = translator.getAllTrackedObjects();
        assertEquals(0, all.size());
    }

    class TestConsumer implements Consumer<Object> {
        public Object accepted = null;

        public void accept(Object t) {
            accepted = t;
        }
    };

    /*
     * Any encoding that creates a reference (w/o @Transient) will trigger the 
     * references listeners.
     */
    @Test
    public void test_reference_listener() throws JJJRMIException {
        Translator translator = new Translator();
        TestConsumer lst = new TestConsumer();
        translator.addReferenceListener(lst);
        None none = new None();
        translator.encode(none);
        assertEquals(none, lst.accepted);
    }

    /**
     * The encoded object both extends JJJObject and has the annotation. The
     * object will not be tracked by the translator, and decoding will produce a
     * new object.
     *
     * @throws JJJRMIException
     */
    @Test
    public void test_do_not_retain_extends() throws JJJRMIException {
        Translator translator = new Translator();
        DoNotRetainExtends object = new DoNotRetainExtends();
        TranslatorResult encoded = translator.encode(object);
        Object decoded = translator.decode(encoded.toString()).getRoot();
        assertFalse(translator.hasReferredObject(object));
        assertNotEquals(object, decoded);
    }

    /**
     * The encoded object only has the annotation. The object will not be
     * tracked by the translator, and decoding will produce a new object.
     *
     * @throws JJJRMIException
     */
    @Test
    public void test_do_not_retain_anno() throws JJJRMIException {
        Translator translator = new Translator();
        DoNotRetainAnno object = new DoNotRetainAnno();
        TranslatorResult encoded = translator.encode(object);
        Object decoded = translator.decode(encoded.toString()).getRoot();
        assertFalse(translator.hasReferredObject(object));
        assertNotEquals(object, decoded);
    }

    /**
     * Transient annotations prevent the field from being encoded. When this
     * object is encoded the default value set in the constructor will remain,
     * while any value set after will be ignored.   The non-transient field will
     * not be ignored.
     *
     * @author Ed Armstrong
     */
    @Test
    public void test_transient_field() throws JJJRMIException {
        Translator translator = new Translator();
        TransientField object = new TransientField();
        object.set(9);
        TranslatorResult encoded = translator.encode(object);        
        translator.clear();
        TransientField decoded = (TransientField) translator.decode(encoded.toString()).getRoot();
        assertNotEquals(decoded.getTransientField(), object.getTransientField());
        assertEquals(decoded.getNonTransientField(), object.getNonTransientField());
    }
    
    /*
     * The class uses a custom hash code. Sending a different object with the 
     * same value will result in a reference being sent.  The decoding of which
     * will be the first object sent.
     */
    @SuppressWarnings("unchecked")
    public void test_custom_hash_code() throws JJJRMIException {
        Translator translator = new Translator();
        CustomHash<Integer> customHash1 = new CustomHash<>();
        CustomHash<Integer> customHash2 = new CustomHash<>();
        customHash1.set(5);
        customHash2.set(5);
        translator.encode(customHash1).toString();
        String encoded = translator.encode(customHash1).toString();
        
        CustomHash<Integer> decoded = (CustomHash<Integer>) translator.decode(encoded).getRoot();
        assertEquals(System.identityHashCode(customHash1), System.identityHashCode(decoded));
    }
    
    /*
     * The class uses a custom hash code. Sending the same object with a 
     * different value will not result in a new encoding.
     */
    @SuppressWarnings("unchecked")
    public void test_custom_hash_code_new_value() throws JJJRMIException {
        Translator translator = new Translator();
        CustomHash<Integer> customHash = new CustomHash<>();
        customHash.set(5);        
        String encoded1 = translator.encode(customHash).toString();
        customHash.set(6);
        String encoded2 = translator.encode(customHash).toString();
        
        CustomHash<Integer> decoded1 = (CustomHash<Integer>) translator.decode(encoded1).getRoot();
        CustomHash<Integer> decoded2 = (CustomHash<Integer>) translator.decode(encoded2).getRoot();
        assertEquals(System.identityHashCode(decoded1), System.identityHashCode(decoded2));
    }    
}
