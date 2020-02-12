package ca.frar.jjjrmi.translator.encoder;
import static ca.frar.jjjrmi.Global.LOGGER;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.annotations.Transient;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.socket.JJJObject;
import ca.frar.jjjrmi.translator.Constants;
import ca.frar.jjjrmi.translator.Translator;
import ca.frar.jjjrmi.translator.decoder.Decoder;
import java.lang.reflect.Field;
import java.util.HashMap;
import org.json.JSONObject;

/**
 *
 * @author Ed Armstrong
 */
@JJJ(insertJJJMethods=false)
abstract public class AHandler<T> {
    private final EncodedResult encodedResult;
    private JSONObject jsonFields;
    private HashMap<String, Field> fields = new HashMap<>();

    @NativeJS
    public AHandler(EncodedResult encodedResult){
        this.encodedResult = encodedResult;        
    }
    
    @NativeJS
    public final EncodedObject doEncode(Object object) throws EncoderException{
        EncodedObject encodedObject = new EncodedObject(object, encodedResult);
        this.jsonFields = encodedObject.fields;
        this.encode((T) object);
        return encodedObject;
    }
    
    @NativeJS
    public final T doDecode(Object t, JSONObject json) throws DecoderException{
        this.jsonFields = json.getJSONObject(Constants.FieldsParam);
        this.setupFields(t.getClass());
        this.decode((T)t);
        return (T)t;
    }

    abstract public T getInstance();
    
    abstract public void decode(T t) throws DecoderException;

    abstract public void encode(T object) throws EncoderException;

    @NativeJS
    public final <T> T decodeField(String fieldName, Class<?> type) throws DecoderException {  
        JSONObject jsonField = jsonFields.getJSONObject(fieldName);
        Translator translator = encodedResult.getTranslator();
        Object decoded = new Decoder(jsonField, translator, type).decode();
        return (T) decoded;
    }

    /**
     * Will encode 'value' and set 'field' to that value.
     * @param name
     * @param value
     * @throws EncoderException 
     */
    @NativeJS
    public final void encodeField(String field, Object value) throws EncoderException {
        JSONObject toJSON = new Encoder(value, this.encodedResult).encode();
        this.jsonFields.put(field, toJSON);
    }
    
    private void setupFields(Class<?> aClass) {
        Class<?> current = aClass;
        while (current != Object.class && current != JJJObject.class) {
            for (Field field : current.getDeclaredFields()) {
                if (field.getAnnotation(Transient.class) != null) continue;
                field.setAccessible(true);
                this.fields.put(field.getName(), field);
            }
            current = current.getSuperclass();
        }
    }
}
