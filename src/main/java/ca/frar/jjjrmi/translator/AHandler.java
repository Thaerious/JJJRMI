package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.jsbuilder.RequireRecord;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author Ed Armstrong
 */
abstract public class AHandler<T> {
    private TranslatorResult translatorResult;
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
        this.decode((T)t);
        return (T)t;
    }
    
    public final static RequireRecord getRequireRecord(Class<? extends AHandler<?>> aClass){
        try {
            Constructor<? extends AHandler<?>> constructor = aClass.getConstructor(TranslatorResult.class);
            AHandler<?> handler = constructor.newInstance((Object)null);
            return handler.getRequire();
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(AHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    abstract public RequireRecord getRequire();
    
    abstract public T getInstance();
    
    /**
     * @param t The object that was returned by 'getInstance'.
     * @throws DecoderException 
     */
    abstract public void decode(T t) throws DecoderException;

    abstract public void encode(T t) throws JJJRMIException;

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
    
    /**
     * When true the client and server will retain a copy of instances of
     * this class. On subsequent sends the copy will be used. When set to false
     * the whole instance is encoded and sent every time.  This is parallel to 
     * the @JJJ(retain) annotation setting.  Default value is true.
     * 
     * @return true, if object is to be tracked.
     */
    public boolean isRetained(){
        return true;
    }    
}
