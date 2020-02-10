package ca.frar.jjjrmi.translator.decoder;

import static ca.frar.jjjrmi.Global.LOGGER;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.translator.Constants;
import ca.frar.jjjrmi.translator.Translator;
import java.lang.reflect.Array;
import org.json.JSONArray;
import org.json.JSONObject;

class ArrayDecoder {
    private final JSONObject json;
    private Class<?> componentClass;
    private Object result;
    private final Translator translator;
    private final JSONArray elements;

    ArrayDecoder(JSONObject json, Translator translator, Class<?> componentClass) throws DecoderException {
        if (json == null) throw new NullPointerException();
        if (translator == null) throw new NullPointerException();

        this.json = json;
        this.translator = translator;
        this.elements = json.getJSONArray(Constants.ElementsParam);   
        this.componentClass = translateComponentClass(componentClass);
    }

    public Object decode() throws DecoderException {
        JSONArray jsonArray = json.getJSONArray(Constants.ElementsParam);
        int[] dims = new int[1];
        dims[0] = jsonArray.length();        
        this.result = Array.newInstance(this.componentClass, dims);
        translator.addTempReference(json.get(Constants.KeyParam).toString(), this.result);

        for (int i = 0; i < elements.length(); i++) {
            JSONObject element = elements.getJSONObject(i);
            Object decoded = new Decoder(element, this.translator, this.componentClass).decode();
            Array.set(this.result, i, decoded);
        }
        return this.result;
    }

    private Class<?> translateComponentClass(Class<?> aClass) {
        if (aClass == null) return Object.class;
        
        switch (aClass.getCanonicalName()) {
            case "boolean":
                return Boolean.TYPE;
            case "byte":
                return Byte.TYPE;
            case "char":
                return Character.TYPE;
            case "short":
                return Short.TYPE;
            case "long":
                return Long.TYPE;
            case "float":
                return Float.TYPE;
            case "double":
                return Double.TYPE;
            case "int":
                return Integer.TYPE;
            default:
                return aClass;
        }
    }

    /**
     * Return true if 'json' is an encoded array.
     * @param json
     * @return 
     */
    public static boolean test(JSONObject json) {
        return json.has(Constants.ElementsParam);
    }
}
