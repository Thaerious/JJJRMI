package ca.frar.jjjrmi.translator.decoder;

import static ca.frar.jjjrmi.Global.LOGGER;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.MissingReferenceException;
import ca.frar.jjjrmi.exceptions.UnknownEncodingException;
import ca.frar.jjjrmi.translator.Constants;
import ca.frar.jjjrmi.translator.Translator;
import org.json.JSONObject;

/**
 * Use for decoding everything except objects.
 *
 * @author Ed Armstrong
 */
class Decoder {

    private final JSONObject json;
    private final Translator translator;
    private Class<?> expectedType = null;

    Decoder(JSONObject json, Translator translator, Class<?> expectedType) {
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
    public Object decode() throws DecoderException {
        LOGGER.debug(json);
        LOGGER.debug(json.has(Constants.PointerParam));
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
        } else if (ArrayDecoder.test(json)) {
            return new ArrayDecoder(json, translator, expectedType).decode();
        }
        throw new UnknownEncodingException(json);
    }
}
