package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.translator.TranslatorResult;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.annotations.Transient;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
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
class EncodedObject {    
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JSONObject");
    private final Object object;
    protected final JSONObject json;
    private final TranslatorResult encodedResult;

    @NativeJS
    EncodedObject(Object object, TranslatorResult encodedResult, boolean retain) throws EncoderException {        
        this.object = object;
        this.json = new JSONObject();
        this.encodedResult = encodedResult;        
        
        this.json.put(Constants.KeyParam, encodedResult.getTranslator().allocReference(object, retain));
        this.json.put(Constants.TypeParam, object.getClass().getName());
        this.json.put(Constants.FieldsParam, new JSONObject());
    }

    /**
    Will only encode @JJJ annotated classes.
    @throws IllegalArgumentException
    @throws IllegalAccessException
    */
    @NativeJS
    void encode() throws IllegalArgumentException, IllegalAccessException, JJJRMIException {
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
    void setFieldData(String name, JSONObject json) throws EncoderException, IllegalArgumentException, IllegalAccessException {
        this.json.getJSONObject(Constants.FieldsParam).put(name, json);
    }    
    
    @NativeJS
    void setField(Field field) throws JJJRMIException, IllegalArgumentException, IllegalAccessException {
        field.setAccessible(true);
        if (field.getAnnotation(Transient.class) != null) return;
        if (Modifier.isStatic(field.getModifiers())) return;        
        
        JSONObject toJSON = new Encoder(field.get(this.object), this.encodedResult).encode();
        this.setFieldData(field.getName(), toJSON);
    }
    
    String getKey(){
        return this.json.getString(Constants.KeyParam);
    }

    JSONObject toJSON(){
        return this.json;
    }
}
