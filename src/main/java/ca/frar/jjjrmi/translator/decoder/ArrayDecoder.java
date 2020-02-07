package ca.frar.jjjrmi.translator.decoder;
import ca.frar.jjjrmi.exceptions.CompletedDecoderException;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.translator.Constants;
import ca.frar.jjjrmi.translator.Translator;
import java.lang.reflect.Array;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONArray;
import org.json.JSONObject;

class ArrayDecoder {
    private int index = 0;
    private final JSONObject json;
    private Class<?> componentClass;
    private Object result;
    private final Translator translator;
    private final JSONArray elements;
    
    ArrayDecoder(JSONObject json, Translator translator, Class<?> aClass) {
        this.json = json;
        this.translator = translator;
        this.elements = json.getJSONArray(Constants.ElementsParam);
        this.componentClass = aClass;
    }

    public Object decode() throws DecoderException {
        JSONArray jsonArray = json.getJSONArray(Constants.ElementsParam);
        this.result = this.instantiateArray(this.componentClass, jsonArray.length());
        
        while(this.index < elements.length()){
            Object element = elements.get(index);
            new Decoder((JSONObject) element, this.translator).decode();
        }
        return this.result;
    }

    private Object instantiateArray(Class<?> aClass, int size) {
        int[] dims = new int[1];
        dims[0] = size;

        Class <?> current = aClass.getComponentType();
        switch (current.getCanonicalName()) {
            case "boolean":
                this.componentClass = Boolean.TYPE;
                break;
            case "byte":
                this.componentClass = Byte.TYPE;
                break;
            case "char":
                this.componentClass = Character.TYPE;
                break;
            case "short":
                this.componentClass = Short.TYPE;
                break;
            case "long":
                this.componentClass = Long.TYPE;
                break;
            case "float":
                this.componentClass = Float.TYPE;
                break;
            case "double":
                this.componentClass = Double.TYPE;
                break;
            case "int":
                this.componentClass = Integer.TYPE;
                break;
            default:
                this.componentClass = current;
                break;
        }

        return Array.newInstance(this.componentClass, dims);
    }
}
