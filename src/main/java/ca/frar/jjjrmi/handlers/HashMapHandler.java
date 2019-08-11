package ca.frar.jjjrmi.handlers;
import ca.frar.jjjrmi.translator.EncodeHandler;
import ca.frar.jjjrmi.translator.EncoderException;
import ca.frar.jjjrmi.translator.Handler;
import ca.frar.jjjrmi.translator.RestoreHandler;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class HashMapHandler implements Handler <HashMap<?,?>>{
    @Override
    public void jjjEncode(EncodeHandler handler,  HashMap<?,?> hashMap) throws IllegalArgumentException, IllegalAccessException, EncoderException {
        Object[] keys = new Object[hashMap.size()];
        Object[] values = new Object[hashMap.size()];

        int i = 0;
        for (Object key : hashMap.keySet()){
            Object value = hashMap.get(key);
            keys[i] = key;
            values[i] = value;
            i++;
        }

        handler.setField("keys", keys);
        handler.setField("values", values);
    }

    @Override
    public void jjjDecode(RestoreHandler handler, HashMap hashMap) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {

        Object[] keys = (Object[]) handler.decodeField("keys");
        Object[] values = (Object[]) handler.decodeField("values");

        for (int i = 0; i < keys.length; i++){
            hashMap.put(keys[i], values[i]);
        }
    }

    @Override
    public HashMap<?, ?> instatiate() {
        return new HashMap<>();
    }
}