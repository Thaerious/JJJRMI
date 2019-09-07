package ca.frar.jjjrmi.test.testable.handlers;
import ca.frar.jjjrmi.annotations.Handles;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.translator.AHandler;
import ca.frar.jjjrmi.translator.EncodeHandler;
import ca.frar.jjjrmi.translator.RestoreHandler;
import java.util.HashMap;
import ca.frar.jjjrmi.translator.IHandler;
import ca.frar.jjjrmi.translator.Translator;
import org.json.JSONObject;

@Handles("java.util.HashMap")
public class HashMapHandler extends AHandler <HashMap<?,?>>{
    
    public HashMapHandler(JSONObject json, Translator translator){
        super(json, translator);
    }
    
    @Override
    public void jjjEncode(HashMap<?,?> hashMap) throws EncoderException {
        Object[] keys = new Object[hashMap.size()];
        Object[] values = new Object[hashMap.size()];

        int i = 0;
        for (Object key : hashMap.keySet()){
            Object value = hashMap.get(key);
            keys[i] = key;
            values[i] = value;
            i++;
        }

        setField("keys", keys);
        setField("values", values);
    }

    @Override
    public void jjjDecode(HashMap hashMap) throws DecoderException {

        Object[] keys = (Object[]) decodeField("keys");
        Object[] values = (Object[]) decodeField("values");

        for (int i = 0; i < keys.length; i++){
            hashMap.put(keys[i], values[i]);
        }
    }

    @Override
    public HashMap<?, ?> instatiate() {
        return new HashMap<>();
    }
}