package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.annotations.Transient;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.socket.JJJObject;
import java.lang.reflect.Field;
import java.util.HashMap;
import org.json.JSONObject;

/**
 *
 * @author Ed Armstrong
 */
abstract public class AHandler<T> {
    private TranslatorResult translatorResult;
    private HashMap<String, Field> fields = new HashMap<>();
    private T instance;
    private EncodedObject encodedObject;
    private JSONObject json;
    
    public AHandler(TranslatorResult translatorResult){
        this.translatorResult = translatorResult;        
    }

    public final T doGetInstance(){
        this.instance = this.getInstance();
        return this.instance;
    }
    
    public final EncodedObject doEncode(Object object) throws JJJRMIException{
        this.encodedObject = new EncodedObject(object, translatorResult, this.isRetained());        
        this.encode((T) object);
        return encodedObject;
    }
    
    public final T doDecode(Object t, JSONObject json) throws DecoderException{
        this.json = json;
        this.setupFields(t.getClass());
        this.decode((T)t);
        return (T)t;
    }

    public boolean isRetained(){
        return true;
    }
    
    abstract public T getInstance();
    
    /**
     * @param t The object that was returned by 'getInstance'.
     * @throws DecoderException 
     */
    abstract public void decode(T t) throws DecoderException;

    abstract public void encode(T t) throws JJJRMIException;

    /**
     * Decode the JSON field 'jsonFieldName' and place the result into
     * 'pojoFieldName' of the object returned from 'getInstance'.
     * @param <T>
     * @param jsonFieldName
     * @param pojoFieldName
     * @return
     * @throws DecoderException 
     */
    public final <T> T decodeField(String jsonFieldName, String pojoFieldName) throws DecoderException {  
        Field field = this.fields.get(pojoFieldName);        
        JSONObject jsonField = this.json.getJSONObject(Constants.FieldsParam).getJSONObject(jsonFieldName);
        Translator translator = translatorResult.getTranslator();
        Class<?> type = field.getType();
        Object decoded = new Decoder(jsonField, translator, type).decode();
        
        try {
            field.set(this.instance, decoded);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new DecoderException(ex);
        }
        
        return (T) decoded;
    }

    /**
     * Decode the field 'jsonFieldName' and return the resulting object.
     * @param <T>
     * @param jsonFieldName the 'name' used for the corresponding 'encodeField'
     * call.
     * @return 
     */
    public final <T> T decodeObject(Class<T> type, String jsonFieldName) throws DecoderException{
        JSONObject jsonField = this.json.getJSONObject(Constants.FieldsParam).getJSONObject(jsonFieldName);
        Translator translator = translatorResult.getTranslator();
        Object decoded = new Decoder(jsonField, translator, type).decode(); 
        return (T) decoded; /* todo add more generics down the call stack? */
    }
    
    /**
     * Will encode the object 'value' as JSON, and set the JSON field 'field'
     * to it.
     * @param name
     * @param value
     * @throws EncoderException 
     */
    public final void encodeField(String name, Object value) throws JJJRMIException {
        try {
            JSONObject toJSON = new Encoder(value, this.translatorResult).encode();
            this.encodedObject.setFieldData(name, toJSON);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new EncoderException(ex);
        }
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
