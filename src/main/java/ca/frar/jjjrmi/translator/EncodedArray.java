package ca.frar.jjjrmi.translator;
import java.lang.reflect.Array;
import org.json.JSONArray;

public class EncodedArray extends EncodedJSON{
    private Object object = null;
    private final JSONArray elements;

    EncodedArray(Object object, Translator translator) throws IllegalArgumentException, IllegalAccessException, EncoderException {
        super(translator);
        this.object = object;
        this.elements = new JSONArray();
        this.put(Constants.ElementsParam, elements);
        encode();
    }

    private void encode() throws IllegalArgumentException, IllegalAccessException, EncoderException {
        for (int i = 0; i < (Array.getLength(object)); i++) {
            Object element = Array.get(object, i);
            elements.put(new Encoder(element, translator).encode());
        }
    }
}