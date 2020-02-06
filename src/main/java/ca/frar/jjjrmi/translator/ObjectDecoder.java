package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.translator.encoder.AHandler;
import ca.frar.jjjrmi.exceptions.CompletedDecoderException;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.IncompleteDecoderException;
import ca.frar.jjjrmi.exceptions.NewHandlerException;
import ca.frar.jjjrmi.exceptions.TranslatorException;
import ca.frar.jjjrmi.utility.JJJOptionsHandler;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import org.json.JSONObject;

public class ObjectDecoder implements IDecoder{
    private Class<?> aClass;
    private final JSONObject json;
    private final Translator translator;
    private Object result;
    private List<String> fieldNames;
    private boolean setup = false;

    ObjectDecoder(JSONObject json, Translator translator) {
        this.json = json;
        this.translator = translator;       
    }

    /**
     * When complete, will retrieve the result of the decoding.
     */
    @Override
    public Object getObject() throws IncompleteDecoderException {
        if (!this.isComplete()) throw new IncompleteDecoderException();
        return this.result;
    }

    @Override
    public boolean isComplete() {
        return this.fieldNames.isEmpty();
    }

    @Override
    public boolean decode() throws DecoderException {
        if (this.isComplete()) throw new CompletedDecoderException();
        
        try {
            if (!setup) this.setupDecode();
            return this.__decode();
        } catch (TranslatorException | NoSuchMethodException | ClassNotFoundException | InstantiationException | IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
            throw new DecoderException(ex);
        }
    }

    /**
     * Runs once to prepare for decoding, does not get called for resume.
     */
    private void setupDecode() throws ClassNotFoundException, NewHandlerException, DecoderException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        this.setup = true;
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
    }

    /**
     * Initialize a new object and fill it's fields.
     *
     * @return
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    private final boolean __decode() throws ClassNotFoundException, InstantiationException, DecoderException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, TranslatorException {
        while(!this.fieldNames.isEmpty()){
            String fieldName = fieldNames.get(0);
            Field field = getJavaField(aClass, fieldName);
            if (field == null) throw new DecoderException("Could not find field '" + fieldName + "' in object '" + aClass.getCanonicalName() + "'");
            JSONObject fields = this.json.getJSONObject(Constants.FieldsParam);
            boolean decoded = new Decoder(fields.getJSONObject(fieldName), translator).decode();
            if (!decoded){
                this.fieldNames.add(fieldName);
                return false;
            }            
        }
        return true;
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
