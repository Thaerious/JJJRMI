package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.exceptions.CompletedDecoderException;
import ca.frar.jjjrmi.exceptions.DecoderException;
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
    private boolean isStarted = false;
    
    ArrayDecoder(JSONObject json, Translator translator, Class<?> aClass) {
        this.json = json;
        this.translator = translator;
        this.elements = json.getJSONArray(Constants.ElementsParam);
        this.componentClass = aClass;
    }

    public boolean decode() throws DecoderException {
        if (this.isComplete()) throw new CompletedDecoderException();
        if (!isStarted) this.decodeSetup();
        return restore();
    }

    private void decodeSetup(){
        isStarted = true;
        JSONArray jsonArray = json.getJSONArray(Constants.ElementsParam);
        result = this.instantiateArray(this.componentClass, jsonArray.length());
    }

    private boolean isComplete() {
        return index >= elements.length();
    }
    
    private boolean restore() throws DecoderException {
        while(this.index < elements.length()){
            Object element = elements.get(index);
            boolean decoded = new Decoder((JSONObject) element, this.translator).decode();
            if (!decoded) break;
        }
        
        return index >= elements.length();
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
