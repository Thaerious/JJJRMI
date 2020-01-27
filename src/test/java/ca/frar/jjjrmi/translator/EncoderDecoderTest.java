package ca.frar.jjjrmi.translator;

import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.testable.ArrayWrapper;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author edward
 */
public class EncoderDecoderTest {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JJJRMI");
    
    /*
     * Arrays are always temporary references.
     * When decoded will return a new array.
     */
    @Test
    public void test_array_as_root() throws JJJRMIException {
        Translator translator = new Translator();
        int[] array = {1, 2, 3, 4};
        EncodedJSON encoded = translator.encode(array);
        Object decodedArray = translator.decode(encoded);
        assertFalse(array.hashCode() == decodedArray.hashCode());
    }
    
    /*
     * When decoded reference is returned.
     */
    @Test
    public void test_array_as_field_00() throws JJJRMIException {
        Translator translator = new Translator();
        ArrayWrapper arrayWrapper = new ArrayWrapper();
        EncodedJSON encoded = translator.encode(arrayWrapper);
        ArrayWrapper decoded = (ArrayWrapper) translator.decode(encoded);
        assertEquals(arrayWrapper.hashCode(), decoded.hashCode());        
    }    
    
    /*
     * When decoded reference is returned. 
     * The change in the encoding is ignored.
     */
    @Test
    public void test_array_as_field_01() throws JJJRMIException {
        Translator translator = new Translator();
        ArrayWrapper arrayWrapper = new ArrayWrapper();
        EncodedJSON encoded = translator.encode(arrayWrapper);
        encoded
            .getJSONObject(Constants.FieldsParam)
            .getJSONObject("arrayField")
            .getJSONArray(Constants.ElementsParam)
            .remove(0);
        ArrayWrapper decoded = (ArrayWrapper) translator.decode(encoded);
        assertEquals(arrayWrapper.hashCode(), decoded.hashCode());        
    } 
    
    /*
     * Because the translator was cleared between encode/decode a new object is made.
     */
    @Test
    public void test_array_as_field_02() throws JJJRMIException {
        Translator translator = new Translator();
        ArrayWrapper arrayWrapper = new ArrayWrapper();
        EncodedJSON encoded = translator.encode(arrayWrapper);

        encoded
            .getJSONObject(Constants.FieldsParam)
            .getJSONObject("arrayField")
            .getJSONArray(Constants.ElementsParam)
            .remove(0);

        translator.clear();
        assertFalse(translator.hasReference("S0"));
        ArrayWrapper decoded = (ArrayWrapper) translator.decode(encoded);
        assertNotEquals(arrayWrapper.hashCode(), decoded.hashCode());        
        assertEquals("[1, 2, 3, 5]", Arrays.toString(decoded.arrayField));
    }        
}