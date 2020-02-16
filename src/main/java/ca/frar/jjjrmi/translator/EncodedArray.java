package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.translator.TranslatorResult;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.translator.Constants;
import java.lang.reflect.Array;
import org.json.JSONArray;
import org.json.JSONObject;

@JJJ(insertJJJMethods=false)
public class EncodedArray extends JSONObject{
    private final TranslatorResult encodedResult;
    private Object object = null;
    private final JSONArray elements;

    @NativeJS
    EncodedArray(Object object, TranslatorResult encodedResult) throws EncoderException {
        this.encodedResult = encodedResult;
        this.object = object;
        this.elements = new JSONArray();
        this.put(Constants.KeyParam, encodedResult.getTranslator().allocReference(object));
        this.put(Constants.RetainParam, false);
        encodedResult.getTranslator().addTempReference(this.get(Constants.KeyParam).toString(), object);
        this.put(Constants.ElementsParam, elements);
        encode();
    }

    @NativeJS
    private void encode() throws EncoderException {
        for (int i = 0; i < (Array.getLength(object)); i++) {
            Object element = Array.get(object, i);
            elements.put(new Encoder(element, encodedResult).encode());
        }
    }
}