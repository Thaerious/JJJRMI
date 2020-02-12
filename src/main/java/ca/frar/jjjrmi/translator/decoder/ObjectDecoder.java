package ca.frar.jjjrmi.translator.decoder;

import static ca.frar.jjjrmi.Global.LOGGER;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.annotations.Transient;
import ca.frar.jjjrmi.translator.decoder.Decoder;
import ca.frar.jjjrmi.translator.encoder.AHandler;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.MissingConstructorException;
import ca.frar.jjjrmi.exceptions.UnknownClassException;
import ca.frar.jjjrmi.socket.JJJObject;
import ca.frar.jjjrmi.translator.Constants;
import ca.frar.jjjrmi.translator.HandlerFactory;
import ca.frar.jjjrmi.translator.Translator;
import ca.frar.jjjrmi.translator.encoder.EncodedResult;
import ca.frar.jjjrmi.utility.JJJOptionsHandler;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.json.JSONObject;

@JJJ(insertJJJMethods=false)
public class ObjectDecoder {
    private Class<?> aClass;
    private final EncodedResult encodedResult;
    private AHandler<?> handler;
    private final JSONObject json;
    private final Translator translator;
    private Object result;
    private List<String> fieldNames;
    private HashMap<String, Field> fields = new HashMap<>();

    @NativeJS
    public ObjectDecoder(EncodedResult encodedResult, JSONObject json, Translator translator) {
        this.json = json;
        this.translator = translator;
        this.encodedResult = encodedResult;
    }

    /**
     * Runs once to prepare for decoding.
     */
    @NativeJS
    public void makeReady() throws DecoderException {
        try {
            this.aClass = Class.forName(json.getString(Constants.TypeParam));
            if (this.aClass == null) {
                throw new UnknownClassException(json.getString(Constants.TypeParam));
            }
            if (HandlerFactory.getInstance().hasHandler(this.aClass)) {
                Class<? extends AHandler<?>> handlerClass = HandlerFactory.getInstance().getHandler(this.aClass);
                this.handler = handlerClass.getConstructor(EncodedResult.class).newInstance(this.encodedResult);
                this.result = handler.getInstance();
            } else {
                this.setupFields();
                Constructor<?> constructor = this.aClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                this.result = constructor.newInstance();
                fieldNames = new LinkedList<>(json.getJSONObject(Constants.FieldsParam).keySet());
            }
            if (new JJJOptionsHandler(this.aClass).retain()) {
                translator.addReference(json.get(Constants.KeyParam).toString(), this.result);
            } else {
                translator.addTempReference(json.get(Constants.KeyParam).toString(), this.result);
            }
        } catch (SecurityException | ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            throw new DecoderException(ex);
        } catch (NoSuchMethodException ex) {
            throw new MissingConstructorException(this.aClass);
        }
    }

    /**
     * Fill in the object's fields.
     */
    @NativeJS
    public final void decode() throws DecoderException {
        if (HandlerFactory.getInstance().hasHandler(this.aClass)) {
            this.handler.doDecode(this.result, json);
        } else {
            for (String fieldName : this.fieldNames) {
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
    }

    @NativeJS
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
