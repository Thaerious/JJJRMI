package ca.frar.jjjrmi.translator.encoder;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.annotations.Transient;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.socket.JJJObject;
import ca.frar.jjjrmi.translator.Constants;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.json.JSONObject;

/**
 * A POJO encoded as JSON.
 * 
 * {
 *      key: reference value
 *      retain: true/false,
 *      type: classname,
 *      fields: fields object
 * }
 * @author Ed Armstrong
 */
@JJJ(insertJJJMethods=false)
public class EncodedObject extends JSONObject{    
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JSONObject");
    private final Object object;
    protected final JSONObject fields;
    private final EncodedResult encodedResult;

    @NativeJS
    public EncodedObject(Object object, EncodedResult encodedResult) throws EncoderException {        
        this.object = object;
        this.fields = new JSONObject();
        this.encodedResult = encodedResult;        
        
        this.put(Constants.KeyParam, encodedResult.getTranslator().allocReference(object));
        this.put(Constants.TypeParam, object.getClass().getName());
        this.put(Constants.FieldsParam, fields);
    }
    
    /**
    Will only encode @JJJ annotated classes.
    @throws IllegalArgumentException
    @throws IllegalAccessException
    */
    @NativeJS
    public void encode() throws IllegalArgumentException, IllegalAccessException, EncoderException {
        LOGGER.trace("EncodedObject.encode() : " + this.object.getClass().getSimpleName());        

        // encode all fields for each class and superclass until JJJObject or Object is reached        
        Class<?> aClass = object.getClass();
        while(aClass != JJJObject.class && aClass != Object.class){
            for (Field field : aClass.getDeclaredFields()) {
                this.setField(field);
            }
            aClass = aClass.getSuperclass();
        }
        
        encodedResult.getTranslator().notifyEncode(object);
    }
    
    @NativeJS
    public void setField(Field field) throws EncoderException, IllegalArgumentException, IllegalAccessException {
        field.setAccessible(true);
        if (field.getAnnotation(Transient.class) != null) return;
        if (Modifier.isStatic(field.getModifiers())) return;        
        
        JSONObject toJSON = new Encoder(field.get(this.object), this.encodedResult).encode();
        this.fields.put(field.getName(), toJSON);
    }
}
