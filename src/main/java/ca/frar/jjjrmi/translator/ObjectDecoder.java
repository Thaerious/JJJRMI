package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.annotations.AfterDecode;
import ca.frar.jjjrmi.annotations.Transient;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.MissingConstructorException;
import ca.frar.jjjrmi.exceptions.UnknownClassException;
import ca.frar.jjjrmi.rmi.socket.JJJObject;
import ca.frar.jjjrmi.utility.JJJOptionsHandler;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.json.JSONObject;

class ObjectDecoder {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("Decoder");
    private Class<?> aClass;
    private final TranslatorResult encodedResult;
    private AHandler<?> handler;
    private final JSONObject json;
    private final Translator translator;
    private Object result;
    private List<String> fieldNames;
    private HashMap<String, Field> fields = new HashMap<>();

    ObjectDecoder(TranslatorResult encodedResult, JSONObject json) {
        this.json = json;
        this.translator = encodedResult.getTranslator();
        this.encodedResult = encodedResult;
    }

    /**
     * Runs once to prepare for decoding.
     */
    void makeReady() throws DecoderException {
        LOGGER.trace("ObjectDecoder.makeReady() : ");
        LOGGER.trace(this.json.toString(2));
        try {
            boolean retain = true;
            this.aClass = Class.forName(json.getString(Constants.TypeParam));
            if (this.aClass == null) {
                throw new UnknownClassException(json.getString(Constants.TypeParam));
            }
            if (HandlerFactory.getInstance().hasHandler(this.aClass)) {                
                Class<? extends AHandler<?>> handlerClass = HandlerFactory.getInstance().getHandler(this.aClass);
                this.handler = handlerClass.getConstructor(TranslatorResult.class).newInstance(this.encodedResult);
                this.result = handler.doGetInstance();
                retain = handler.isRetained();
                LOGGER.trace(" - handler is retained");
            } else {
                this.setupFields();
                Constructor<?> constructor = this.aClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                this.result = constructor.newInstance();
                fieldNames = new LinkedList<>(json.getJSONObject(Constants.FieldsParam).keySet());
                retain = new JJJOptionsHandler(this.aClass).retain();
                LOGGER.trace(" - object is retained");
            }
            if (retain) {
                translator.addReference(json.getString(Constants.KeyParam), this.result);
            } else {
                translator.addTempReference(json.getString(Constants.KeyParam), this.result);
            }            
            LOGGER.trace(" key: " + json.getString(Constants.KeyParam));
        } catch (SecurityException | ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            throw new DecoderException(ex);
        } catch (NoSuchMethodException ex) {
            throw new MissingConstructorException(this.aClass);
        }
    }

    /**
     * Fill in the object's fields.
     */
    final void decode() throws DecoderException {
        if (HandlerFactory.getInstance().hasHandler(this.aClass)) {
            this.handler.doDecode(this.result, this.json);
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

    void afterDecode() throws InvocationTargetException, IllegalAccessException {
        Object decoder;
        if (HandlerFactory.getInstance().hasHandler(this.aClass)) {
            decoder = this.handler;
        } else {
            decoder = this.result;
        }

        Method[] methods = decoder.getClass().getMethods();
        for (Method method : methods){
            AfterDecode annotation = method.getAnnotation(AfterDecode.class);
            if (annotation != null){
                method.invoke(decoder);
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
