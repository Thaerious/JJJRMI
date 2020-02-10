package ca.frar.jjjrmi.translator.encoder;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.translator.Constants;
import java.lang.reflect.Array;
import org.json.JSONArray;
import org.json.JSONObject;

public class EncodedArray extends JSONObject{
    private final EncodedResult encodedResult;
    private Object object = null;
    private final JSONArray elements;

    EncodedArray(Object object, EncodedResult encodedResult) throws EncoderException {
        this.encodedResult = encodedResult;
        this.object = object;
        this.elements = new JSONArray();
        this.put(Constants.KeyParam, encodedResult.getTranslator().allocReference(object));
        this.put(Constants.RetainParam, false);
//        this.put(Constants.ComponentTypeParam, object.getClass().getComponentType().getCanonicalName());
        encodedResult.getTranslator().addTempReference(this.get(Constants.KeyParam).toString(), object);
        this.put(Constants.ElementsParam, elements);
        encode();
    }

    private void encode() throws EncoderException {
        for (int i = 0; i < (Array.getLength(object)); i++) {
            Object element = Array.get(object, i);
            elements.put(new Encoder(element, encodedResult).encode());
        }
    }
}