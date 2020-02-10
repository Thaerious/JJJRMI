package ca.frar.jjjrmi.translator.decoder;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.UnknownEncodingException;
import ca.frar.jjjrmi.translator.Constants;
import ca.frar.jjjrmi.translator.Translator;
import java.util.Scanner;
import org.json.JSONObject;

/**
 * Use for decoding everything except objects.
 *
 * @author Ed Armstrong
 */
@JJJ(insertJJJMethods=false)
public class Decoder {
    private final JSONObject json;
    private final Translator translator;
    private Class<?> expectedType = null;

    @NativeJS
    public Decoder(JSONObject json, Translator translator, Class<?> expectedType) {
        this.json = json;
        this.translator = translator;
        this.expectedType = expectedType;
    }

    /**
     * Decode the json.
     *
     * @return true if complete
     * @throws DecoderException
     */
    @NativeJS
    public Object decode() throws DecoderException {
        if (json.has(Constants.TypeParam) && json.getString(Constants.TypeParam).equals(Constants.NullValue)) {
            return null;
        } else if (json.has(Constants.PointerParam)) {
            return translator.getReferredObject(json.get(Constants.PointerParam).toString());
        } else if (json.has(Constants.EnumParam)) {
            try {
                Class<? extends Enum> aClass;
                aClass = (Class<? extends Enum>) this.getClass().getClassLoader().loadClass((String) json.get(Constants.EnumParam));
                String value = json.get(Constants.ValueParam).toString();
                Enum valueOf = Enum.valueOf(aClass, value);
                return valueOf;
            } catch (ClassNotFoundException ex) {
                throw new DecoderException(ex);
            }
        } else if (json.has(Constants.ValueParam)) {
            /* the value is a primative, check expected type */
            switch (expectedType.getCanonicalName()) {
                case "java.lang.String":
                    return json.get(Constants.ValueParam).toString();
                case "boolean":
                case "java.lang.Boolean":
                    return json.getBoolean(Constants.ValueParam);
                case "byte":
                case "java.lang.Byte":
                    Integer bite = json.getInt(Constants.ValueParam);
                    return bite.byteValue();
                case "char":
                case "java.lang.Character":
                    return json.get(Constants.ValueParam).toString().charAt(0);
                case "short":
                case "java.lang.Short":
                    Integer shirt = json.getInt(Constants.ValueParam);
                    return shirt.shortValue();
                case "long":
                case "java.lang.Long":
                    return json.getLong(Constants.ValueParam);
                case "float":
                case "java.lang.Float":
                    Double d = json.getDouble(Constants.ValueParam);
                    return d.floatValue();
                case "double":
                case "java.lang.Double":
                    return json.getDouble(Constants.ValueParam);
                case "int":
                case "java.lang.Integer":
                    return json.getInt(Constants.ValueParam);
            }

            /* expected type not found, refer to primitive type */
            String primitive = json.get(Constants.PrimitiveParam).toString();
            String value = json.get(Constants.ValueParam).toString();
            Scanner scanner = new Scanner(value);

            switch (primitive) {
                case "number":
                    if (scanner.hasNextInt()) return scanner.nextInt();
                    if (scanner.hasNextDouble()) return scanner.nextDouble();
                case "string":
                    return value;
                case "boolean":
                    if (scanner.hasNextBoolean()) return scanner.nextBoolean();
            }
        } else if (ArrayDecoder.test(json)) {
            return new ArrayDecoder(json, translator, expectedType.getComponentType()).decode();
        }
        throw new UnknownEncodingException(json);
    }
}
