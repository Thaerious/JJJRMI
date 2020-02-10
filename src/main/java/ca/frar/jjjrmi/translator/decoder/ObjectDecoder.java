package ca.frar.jjjrmi.translator.decoder;

import ca.frar.jjjrmi.annotations.Transient;
import ca.frar.jjjrmi.translator.decoder.Decoder;
import ca.frar.jjjrmi.translator.encoder.AHandler;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.MissingConstructorException;
import ca.frar.jjjrmi.exceptions.UnknownClassException;
import ca.frar.jjjrmi.socket.JJJObject;
import ca.frar.jjjrmi.translator.Constants;
import ca.frar.jjjrmi.translator.Translator;
import ca.frar.jjjrmi.utility.JJJOptionsHandler;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.json.JSONObject;

public class ObjectDecoder {
    private Class<?> aClass;
    private final JSONObject json;
    private final Translator translator;
    private Object result;
    private List<String> fieldNames;
    private HashMap<String, Field> fields = new HashMap<>();

    public ObjectDecoder(JSONObject json, Translator translator) {
        this.json = json;
        this.translator = translator;
    }

    /**
     * Runs once to prepare for decoding, does not get called for resume.
     */
    public void makeReady() throws DecoderException {
        try {
            this.aClass = Class.forName(json.getString(Constants.TypeParam));
            if (this.aClass == null){
                throw new UnknownClassException(json.getString(Constants.TypeParam));
            }
            this.setupFields();
            AHandler handler = null;

            //        if (translator.hasHandler(this.aClass)) {
            /* if handler, create new object with handler */
            //            handler = translator.newHandler(this.aClass, this.json);
            //            this.result = handler.instatiate();
            //            handler.jjjDecode(this.result);
            //        } else {
            /* create new object from description */            
            Constructor<?> constructor = this.aClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            this.result = constructor.newInstance();
            //        }

            if (new JJJOptionsHandler(this.aClass).retain()) {
                translator.addReference(json.get(Constants.KeyParam).toString(), this.result);
            } else {
                translator.addTempReference(json.get(Constants.KeyParam).toString(), this.result);
            }

            fieldNames = new LinkedList<>(json.getJSONObject(Constants.FieldsParam).keySet());
        } catch (SecurityException | ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            throw new DecoderException(ex);
        } catch (NoSuchMethodException ex) {
            throw new MissingConstructorException(this.aClass);
        }
    }

    /**
     * Fill in the object's fields.
     */
    public final void decode() throws DecoderException {
        for (String fieldName : this.fieldNames){
            try {
                Field field = this.fields.get(fieldName);
                if (field == null) throw new DecoderException("Could not find field '" + fieldName + "' in object '" + aClass.getCanonicalName() + "'");
                JSONObject fields = this.json.getJSONObject(Constants.FieldsParam);
                Object fieldValue = new Decoder(fields.getJSONObject(fieldName), translator, field.getType()).decode();
                field.set(result, fieldValue);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new DecoderException(ex);
            }
        }
    }

    private void setupFields() {
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
