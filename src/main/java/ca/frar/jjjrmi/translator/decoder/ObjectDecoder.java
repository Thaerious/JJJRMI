package ca.frar.jjjrmi.translator.decoder;

import ca.frar.jjjrmi.translator.decoder.Decoder;
import ca.frar.jjjrmi.translator.encoder.AHandler;
import ca.frar.jjjrmi.exceptions.CompletedDecoderException;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.IncompleteDecoderException;
import ca.frar.jjjrmi.exceptions.TranslatorException;
import ca.frar.jjjrmi.translator.Constants;
import ca.frar.jjjrmi.translator.Translator;
import ca.frar.jjjrmi.utility.JJJOptionsHandler;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import org.json.JSONObject;

public class ObjectDecoder {

    private Class<?> aClass;
    private final JSONObject json;
    private final Translator translator;
    private Object result;
    private List<String> fieldNames;

    public ObjectDecoder(JSONObject json, Translator translator) {
        this.json = json;
        this.translator = translator;
    }

    /**
     * When complete, will retrieve the result of the decoding.
     */
    public Object getObject() throws IncompleteDecoderException {
        return this.result;
    }

    /**
     * Runs once to prepare for decoding, does not get called for resume.
     */
    public void makeReady() throws DecoderException {
        try {
            this.aClass = Class.forName(json.getString(Constants.TypeParam));
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
        } catch (SecurityException | NoSuchMethodException | ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            throw new DecoderException(ex);
        }
    }

    /**
     * Fill in the object's fields.
     */
    public final void decode() throws DecoderException {
        for (String fieldName : this.fieldNames){
            Field field = getJavaField(aClass, fieldName);
            if (field == null) throw new DecoderException("Could not find field '" + fieldName + "' in object '" + aClass.getCanonicalName() + "'");
            JSONObject fields = this.json.getJSONObject(Constants.FieldsParam);
            new Decoder(fields.getJSONObject(fieldName), translator).decode();
        }
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
