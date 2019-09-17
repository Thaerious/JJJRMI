package ca.frar.jjjrmi.translator;

import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.TranslatorException;
import ca.frar.jjjrmi.utility.JJJOptionsHandler;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import org.json.JSONObject;

public class RestoredObject implements IsRestorable, RestoreHandler {

    private final JSONObject json;
    private final Translator translator;
    private final JSONObject fields;

    RestoredObject(JSONObject json, Translator translator) {
        this.json = json;
        this.translator = translator;
        this.fields = json.getJSONObject(Constants.FieldsParam);
    }

    public final Object decode() throws DecoderException{
        try {
            return this.__decode();
        } catch (TranslatorException | NoSuchMethodException | ClassNotFoundException | InstantiationException | IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
            throw new DecoderException(ex);
        }
    }
    
    /**
    Initialize a new object and fill it's fields.
    @return
    @throws ClassNotFoundException
    @throws InstantiationException
    @throws IllegalAccessException
    @throws NoSuchFieldException
    @throws NoSuchMethodException
    @throws IllegalArgumentException
    @throws InvocationTargetException
     */
    private final Object __decode() throws ClassNotFoundException, InstantiationException, DecoderException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, TranslatorException {
        Class<?> aClass = Class.forName(json.getString(Constants.TypeParam));
        Object newInstance;
        AHandler handler = null;
        
        /* aready restored, retrieve restored object */
        if (json.has(Constants.KeyParam) && translator.hasReference(json.getString(Constants.KeyParam))) {
            newInstance = translator.getReferredObject(json.getString(Constants.KeyParam));
            return newInstance;
        }
        else if (translator.hasHandler(aClass)) {
            /* if handler, create new object with handler */
            handler = translator.newHandler(aClass, this.json);
            newInstance = handler.instatiate();
        }
        else {
            /* create new object from description */
            Constructor<?> constructor = aClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            newInstance = constructor.newInstance();
        }

        JJJOptionsHandler jjjOptions = new JJJOptionsHandler(aClass);
        
        /* if json has a 'retain' record, use it, else use the retain policy set by @JJJ which defaults to true */
        if (this.json.has("retain") && this.json.getBoolean("retain")){
            translator.addReference(json.get(Constants.KeyParam).toString(), newInstance);
        }        
        else if (this.json.has("retain") && !this.json.getBoolean("retain")){
            translator.addTempReference(json.get(Constants.KeyParam).toString(), newInstance);
        }
        else if (jjjOptions.retain()) {
            translator.addReference(json.get(Constants.KeyParam).toString(), newInstance);
        }
        else {
            translator.addTempReference(json.get(Constants.KeyParam).toString(), newInstance);
        }

        if (translator.hasHandler(aClass)) {
            handler.jjjDecode(newInstance);
        } else {
            for (String name : fields.keySet()) {
                EncodedJSON jsonField = new EncodedJSON(this.translator, fields.getJSONObject(name).toString());
                Field field = getJavaField(aClass, name);
                if (field == null) throw new RuntimeException("Could not find field '" + name + "' in object '" + aClass.getCanonicalName() + "'");
                field.setAccessible(true);
                new Decoder(jsonField, translator, field.getType()).decode(obj->{
                    try {
                        field.set(newInstance, obj);
                    } catch (IllegalArgumentException | IllegalAccessException ex) {
                        throw new DecoderException(ex);
                    }
                });
            }
        }

        this.translator.notifyDecode(newInstance);
        return newInstance;
    }

    private Field getJavaField(Class<?> aClass, String name) {
        while (aClass != Object.class) {
            for (Field field : aClass.getDeclaredFields()) {
                if (field.getName().equals(name)) return field;
            }
            aClass = aClass.getSuperclass();
        }
        return null;
    }

    @Override
    public <T> T decodeField(String name) throws DecoderException {
        return (T) this.translator.decode(fields.getJSONObject(name));
    }

    public String getType() {
        return json.getString(Constants.TypeParam);
    }

    @Override
    public String toString() {
        return json.toString();
    }

    public String toString(int indent) {
        return json.toString(indent);
    }
}
