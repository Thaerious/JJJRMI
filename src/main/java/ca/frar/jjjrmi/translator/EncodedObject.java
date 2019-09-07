package ca.frar.jjjrmi.translator;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.json.JSONObject;
import ca.frar.jjjrmi.annotations.Transient;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.socket.JJJObject;
import ca.frar.jjjrmi.utility.JJJOptionsHandler;

class EncodedObject extends EncodedJSON implements EncodeHandler{    
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(Translator.class);
    private final Object object;
    private final JSONObject fields;
    private final Translator translator;

    EncodedObject(Object object, Translator translator) throws EncoderException {        
        super(translator);
        LOGGER.trace("new EncodedObject(" + object.getClass().getSimpleName() + ", translator)");
        this.object = object;
        this.fields = new JSONObject();
        this.translator = translator;

        JJJOptionsHandler jjjOptions = new JJJOptionsHandler(object);

        this.put(Constants.KeyParam, translator.allocNextKey());
        if (jjjOptions.retain()){
            translator.addReference(this.get(Constants.KeyParam).toString(), object);
            this.put(Constants.RetainParam, true);
        } else {
            translator.addTempReference(this.get(Constants.KeyParam).toString(), object);
            this.put(Constants.RetainParam, false);
        }

        this.put(Constants.TypeParam, object.getClass().getName());
        this.put(Constants.FieldsParam, fields);
    }

    /**
    Will only encode @JJJ annotated classes.
    @throws IllegalArgumentException
    @throws IllegalAccessException
    */
    void encode() throws IllegalArgumentException, IllegalAccessException, EncoderException {
        LOGGER.trace("EncodedObject.encode() : " + this.object.getClass().getSimpleName());
        Class<?> aClass = object.getClass();

        while(aClass != JJJObject.class && aClass != Object.class){
            Field[] declaredFields = aClass.getDeclaredFields();

            for (Field field : declaredFields) {
                field.setAccessible(true);
                if (field.getAnnotation(Transient.class) != null) continue;
                if (Modifier.isStatic(field.getModifiers())) continue;
                setField(field.getName(), field.get(object));
            }
            aClass = aClass.getSuperclass();
        }
    }

    @Override
    public void setField(String name, Object value) throws EncoderException {
        EncodedJSON toJSON = new Encoder(value, translator).encode();
        this.fields.put(name, toJSON);
    }
}
