package ca.frar.jjjrmi.translator;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONArray;
import org.json.JSONObject;

class RestoredArray implements IsRestorable {
    private final JSONObject json;
    private Class<?> componentClass;
    private final Translator translator;
    private final JSONArray elements;

    RestoredArray(JSONObject json, Translator translator, Class<?> aClass) {
        this.json = json;
        this.translator = translator;
        this.elements = json.getJSONArray(Constants.ElementsParam);
        this.componentClass = aClass;
    }

    @Override
    public Object decode() throws DecoderException {
        JSONArray jsonArray = json.getJSONArray(Constants.ElementsParam);
        Object newInstance = this.instantiateArray(this.componentClass, jsonArray.length());
        restore(newInstance);
        return newInstance;
    }

    private void restore(Object arrayInstance) throws DecoderException {
        for (int i = 0; i < elements.length(); i++) {
            Object element = elements.get(i);
            AtomicInteger atomicI = new AtomicInteger (i);

            new Decoder((JSONObject) element, translator, componentClass).decode(
                obj->{
                    Array.set(arrayInstance, atomicI.get(), obj);
                }
            );
        }
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
