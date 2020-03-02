package ca.frar.jjjrmi.translator;

import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.testclasses.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.json.JSONObject;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class JStoJavaTest {

    public static final String FILENAME = "target/test-data/from-js.json";
    private final JSONObject json;

    public JStoJavaTest() throws FileNotFoundException, IOException {
        FileReader fileReader = new FileReader(FILENAME);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        StringBuilder builder = new StringBuilder();
        String line = bufferedReader.readLine();
        while (line != null) {
            builder.append(line);
            line = bufferedReader.readLine();
        }

        this.json = new JSONObject(builder.toString());
    }

    /**
     * Sanity test to make sure the translator loads and processes.
     */
    @Test
    public void test_none() throws DecoderException {
        Translator translator = new Translator();
        String text = this.json.getJSONObject("none").toString();
        Object object = translator.decode(text).getRoot();
        assertEquals(None.class, object.getClass());
    }

    /**
     * A class will be populated with default values.
     */
    @Test
    public void test_simple() throws DecoderException {
        Translator translator = new Translator();
        String text = this.json.getJSONObject("simple").toString();
        Simple object = (Simple) translator.decode(text).getRoot();

        assertEquals(Simple.class, object.getClass());
        assertEquals(5, object.x);
        assertEquals(7, object.y);
        assertEquals(Shapes.CIRCLE, object.shape);
    }

    /**
     * The default values of a class will be overwritten by the data.
     */
    @Test
    public void test_primitives() throws DecoderException {
        Translator translator = new Translator();
        String text = this.json.getJSONObject("primitives").toString();
        Primitives object = (Primitives) translator.decode(text).getRoot();
        assertEquals(Primitives.class, object.getClass());
        assertEquals("alpha9", object.string);
    }

    /**
     * Classes that indirectly extend JJJObject will include exteded fields.
     */
    @Test
    public void test_primitivesExtended() throws DecoderException {
        Translator translator = new Translator();
        String text = this.json.getJSONObject("primitivesExtended").toString();
        PrimitivesExtended object = (PrimitivesExtended) translator.decode(text).getRoot();
        assertEquals(PrimitivesExtended.class, object.getClass());
        assertEquals("alpha16", object.string);
    }

    /**
     * Null fields will be instantiated with null.
     */
    @Test
    public void test_hasNull() throws DecoderException {
        Translator translator = new Translator();
        String text = this.json.getJSONObject("hasNull").toString();
        Has object = (Has) translator.decode(text).getRoot();
        assertEquals(Has.class, object.getClass());
        assertEquals(null, object.get());
    }

    /**
     * Decoding the same object twice will return the same object.
     */
    @Test
    public void test_same() throws DecoderException {
        Translator translator = new Translator();
        String text = this.json.getJSONObject("none").toString();
        Object object1 = translator.decode(text).getRoot();
        Object object2 = translator.decode(text).getRoot();
        assertEquals(object1, object2);
    }

    /**
     * Cached objects can be retrieved by reference. This reference can be the
     * root object.
     */
    @Test
    public void test_referecedRoot() throws DecoderException {
        Translator translator = new Translator();
        String text1 = this.json.getJSONObject("hasRoot").toString();
        Object object1 = translator.decode(text1).getRoot();
        String text2 = this.json.getJSONObject("hasRef").toString();
        Object object2 = translator.decode(text2).getRoot();
        assertEquals(object1, object2);
    }

    /**
     * Cached objects can be retrieved by reference. This reference can be a
     * field object.
     */
    @Test
    public void test_referecedField() throws DecoderException {
        Translator translator = new Translator();
        String text1 = this.json.getJSONObject("hasRoot").toString();
        Has object1 = (Has) translator.decode(text1).getRoot();
        String text2 = this.json.getJSONObject("hasField").toString();
        Has object2 = (Has) translator.decode(text2).getRoot();
        assertEquals(object1, object2.get());
    }

    /**
     * Zero length arrays will be instantiated.
     * It is a known limitation that decoded generic arrays have to be of type
     * Object[].
     */
    @Test
    public void test_emptyArray() throws DecoderException {
        Translator translator = new Translator();
        String text = this.json.getJSONObject("emptyArray").toString();
        Has<Object[]> object = (Has<Object[]>) translator.decode(text).getRoot();
        assertEquals(true, object.get().getClass().isArray());
        assertEquals(0, object.get().length);
    }

    /**
     * Arrays will be filled with decoded values.
     * It is a known limitation that decoded generic arrays have to be of type
     * Object[].
     */
    @Test
    public void test_nonEmptyArray() throws DecoderException {
        Translator translator = new Translator();
        String text = this.json.getJSONObject("nonEmptyArray").toString();
        Has<Object[]> object = (Has<Object[]>) translator.decode(text).getRoot();
        assertEquals(true, object.get().getClass().isArray());
        assertEquals(3, object.get().length);
        assertEquals(1, object.get()[0]);
        assertEquals(3, object.get()[1]);
        assertEquals(7, object.get()[2]);
    }

    /**
     * Circular references will point to each other.
     */
    @Test
    public void test_circular() throws DecoderException {
        Translator translator = new Translator();
        String text = this.json.getJSONObject("circular").toString();
        CircularRef object = (CircularRef) translator.decode(text).getRoot();
        CircularRef target = object.target;

        assertEquals(CircularRef.class, object.getClass());
        assertEquals(object, target.target);
    }

    /**
     * A class with a registered handler, will invoke the handler methods to
     * decode instead of the default.
     *
     * @returns {undefined}
     */
    @Test
    public void test_hasHandler() throws DecoderException {
        Translator translator = new Translator();
        String text = this.json.getJSONObject("handled").toString();
        HasHandler object = (HasHandler) translator.decode(text).getRoot();

        assertEquals(HasHandler.class, object.getClass());
        assertEquals(9, object.z);
    }

    /**
     * The encoded object both extends JJJObject and has the annotation. The
     * object will not be tracked by the translator, and decoding will produce a
     * new object.
     *
     * @throws JJJRMIException
     */
    @Test
    public void test_doNotRetainExtends() throws DecoderException {
        Translator translator = new Translator();
        String text = this.json.getJSONObject("doNotRetainExtends").toString();
        Object object1 = translator.decode(text).getRoot();
        Object object2 = translator.decode(text).getRoot();

        assertEquals(DoNotRetainExtends.class, object1.getClass());
        assertEquals(DoNotRetainExtends.class, object2.getClass());
        assertNotEquals(object1, object2);
    }
}
