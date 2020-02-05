package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.exceptions.CompletedDecoderException;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.IncompleteDecoderException;
import java.util.Scanner;
import org.json.JSONObject;
import static spoon.Launcher.LOGGER;

/**
 * Use for decoding everything except objects.
 * @author Ed Armstrong
 */
class Decoder implements IDecoder{
    private final JSONObject json;
    private final Translator translator;
    private Class<?> expectedType;
    private Object result;
    private boolean complete = false;

    Decoder(JSONObject json, Translator translator) {
        if (json.keySet().isEmpty()) throw new RuntimeException();
        this.json = json;
        this.translator = translator;
    }

    private void setObject(Object object){
        this.result = object;
        this.complete = true;
    }
    
    /**
     * When complete, will retrieve the result of the decoding.
     */
    public Object getObject() throws IncompleteDecoderException{
        if (!this.isComplete()) throw new IncompleteDecoderException();
        return this.result;
    }
    
    public boolean isComplete(){
        return this.complete;
    }

    /**
     * Decode the json.
     * @return true if complete
     * @throws DecoderException 
     */
    public boolean decode() throws DecoderException {
        if (this.isComplete()) throw new CompletedDecoderException();
        
        if (json.has(Constants.TypeParam) && json.getString(Constants.TypeParam).equals(Constants.NullValue)) {
            this.setObject(null);
        } else if (json.has(Constants.PointerParam)) {
            if (!translator.hasReference(json.get(Constants.PointerParam).toString())){
                return false;
            }
            Object referredObject = translator.getReferredObject(json.get(Constants.PointerParam).toString());
            this.setObject(referredObject);
        } else if (json.has(Constants.EnumParam)) {
            Class<? extends Enum> aClass;
            try {
                aClass = (Class<? extends Enum>) this.getClass().getClassLoader().loadClass((String) json.get(Constants.EnumParam));
            } catch (ClassNotFoundException ex) {
                throw new DecoderException(ex);
            }
            String value = json.get(Constants.ValueParam).toString();
            Enum valueOf = Enum.valueOf(aClass, value);
            this.setObject(valueOf);
        } else if (json.has(Constants.ValueParam)) {
            /* the value is a primative, check expected type */
            if (expectedType != null) switch (expectedType.getCanonicalName()) {
                case "java.lang.String":
                    this.setObject(json.get(Constants.ValueParam).toString());
                    return true;
                case "boolean":
                case "java.lang.Boolean":
                    this.setObject(json.getBoolean(Constants.ValueParam));
                    return true;
                case "byte":
                case "java.lang.Byte":
                    Integer bite = json.getInt(Constants.ValueParam);
                    this.setObject(bite.byteValue());
                    return true;
                case "char":
                case "java.lang.Character":
                    this.setObject(json.get(Constants.ValueParam).toString().charAt(0));
                    return true;
                case "short":
                case "java.lang.Short":
                    Integer shirt = json.getInt(Constants.ValueParam);
                    this.setObject(shirt.shortValue());
                    return true;
                case "long":
                case "java.lang.Long":
                    this.setObject(json.getLong(Constants.ValueParam));
                    return true;
                case "float":
                case "java.lang.Float":
                    Double d = json.getDouble(Constants.ValueParam);
                    this.setObject(d.floatValue());
                    return true;
                case "double":
                case "java.lang.Double":
                    this.setObject(json.getDouble(Constants.ValueParam));
                    return true;
                case "int":
                case "java.lang.Integer":
                    this.setObject(json.getInt(Constants.ValueParam));
                    return true;
            }

            /* expected type not found, refer to primitive type */
            String primitive = json.get(Constants.PrimitiveParam).toString();
            String value = json.get(Constants.ValueParam).toString();
            Scanner scanner = new Scanner(value);

            switch (primitive) {
                case "number":
                    if (scanner.hasNextInt()) this.setObject(scanner.nextInt());
                    if (scanner.hasNextDouble()) this.setObject(scanner.nextDouble());
                    return true;
                case "string":
                    this.setObject(value);
                    return true;
                case "boolean":
                    if (scanner.hasNextBoolean()) this.setObject(scanner.nextBoolean());
                    return true;
            }
        } else if (json.has(Constants.ElementsParam)) {
            if (expectedType == null || !expectedType.isArray()) expectedType = Object[].class;
            Object array = new ArrayDecoder(json, translator, expectedType).decode();
            this.setObject(array);
        } else {
            LOGGER.warn(this.json.toString(2));
            throw new RuntimeException("Unknown JSON encoding");
        }
        return true;
    }
}