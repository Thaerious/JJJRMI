package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.translator.TranslatorResult;
import static ca.frar.jjjrmi.Global.LOGGER;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.annotations.Transient;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.socket.JJJObject;
import ca.frar.jjjrmi.translator.Constants;
import ca.frar.jjjrmi.translator.Translator;
import ca.frar.jjjrmi.translator.Decoder;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author Ed Armstrong
 */
@JJJ(insertJJJMethods=false)
abstract public class AHandler<T> {
    private final TranslatorResult encodedResult;
    private HashMap<String, Field> fields = new HashMap<>();
    private T instance;
    private EncodedObject encodedObject;
    
    @NativeJS
    public AHandler(TranslatorResult encodedResult){
        this.encodedResult = encodedResult;        
    }

    @NativeJS
    public final T doGetInstance(){
        this.instance = this.getInstance();
        return this.instance;
    }
    
    @NativeJS
    public final EncodedObject doEncode(Object object) throws EncoderException{
        this.encodedObject = new EncodedObject(object, encodedResult);        
        this.encode((T) object);
        return encodedObject;
    }
    
    @NativeJS
    public final T doDecode(Object t, EncodedObject json) throws DecoderException{
        this.encodedObject = encodedObject;
        this.setupFields(t.getClass());
        this.decode((T)t);
        return (T)t;
    }

    abstract public T getInstance();
    
    abstract public void decode(T t) throws DecoderException;

    abstract public void encode(T object) throws EncoderException;

    @NativeJS
    public final <T> T decodeField(String jsonFieldName, String pojoFieldName) throws DecoderException {  
        Field field = this.fields.get(pojoFieldName);
        JSONObject jsonField = this.encodedObject.getField(jsonFieldName);
        Translator translator = encodedResult.getTranslator();
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
     * Will encode 'value' and set 'field' to that value.
     * @param name
     * @param value
     * @throws EncoderException 
     */
    @NativeJS
    public final void encodeField(String name, Object value) throws EncoderException {
        try {
            JSONObject toJSON = new Encoder(value, this.encodedResult).encode();
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
