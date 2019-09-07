package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.exceptions.DecoderException;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import static spoon.Launcher.LOGGER;

class Decoder {
    private final JSONObject json;
    private final Translator translator;
    private Class<?> expectedType;
    private DecodeConsumer consumer;

    Decoder(JSONObject json, Translator translator, Class<?> expectedType) {
        if (json.keySet().isEmpty()) throw new RuntimeException();
        this.json = json;
        this.translator = translator;
        this.expectedType = expectedType;
    }

    public void resume()  throws DecoderException {
        this.decode(consumer);
    }

    public void decode(DecodeConsumer consumer) throws DecoderException {
        this.consumer = consumer;

        if (json.has(Constants.TypeParam) && json.getString(Constants.TypeParam).equals(Constants.NullValue)) {
            consumer.accept(null);
        } else if (json.has(Constants.PointerParam)) {
            if (!translator.hasReference(json.get(Constants.PointerParam).toString())){
                translator.deferDecoding(this);
            }
            Object referredObject = translator.getReferredObject(json.get(Constants.PointerParam).toString());
            consumer.accept(referredObject);
        } else if (json.has(Constants.EnumParam)) {
            Class<? extends Enum> aClass;
            try {
                aClass = (Class<? extends Enum>) this.getClass().getClassLoader().loadClass((String) json.get(Constants.EnumParam));
            } catch (ClassNotFoundException ex) {
                throw new DecoderException(ex);
            }
            String value = json.get(Constants.ValueParam).toString();
            Enum valueOf = Enum.valueOf(aClass, value);
            consumer.accept(valueOf);
        } else if (json.has(Constants.ValueParam)) {
            /* the value is a primative, check expected type */
            if (expectedType != null) switch (expectedType.getCanonicalName()) {
                case "java.lang.String":
                    consumer.accept(json.get(Constants.ValueParam).toString());
                    return;
                case "boolean":
                case "java.lang.Boolean":
                    consumer.accept(json.getBoolean(Constants.ValueParam));
                    return;
                case "byte":
                case "java.lang.Byte":
                    Integer bite = json.getInt(Constants.ValueParam);
                    consumer.accept(bite.byteValue());
                    return;
                case "char":
                case "java.lang.Character":
                    consumer.accept(json.get(Constants.ValueParam).toString().charAt(0));
                    return;
                case "short":
                case "java.lang.Short":
                    Integer shirt = json.getInt(Constants.ValueParam);
                    consumer.accept(shirt.shortValue());
                    return;
                case "long":
                case "java.lang.Long":
                    consumer.accept(json.getLong(Constants.ValueParam));
                    return;
                case "float":
                case "java.lang.Float":
                    Double d = json.getDouble(Constants.ValueParam);
                    consumer.accept(d.floatValue());
                    return;
                case "double":
                case "java.lang.Double":
                    consumer.accept(json.getDouble(Constants.ValueParam));
                    return;
                case "int":
                case "java.lang.Integer":
                    consumer.accept(json.getInt(Constants.ValueParam));
                    return;
            }

            /* expected type not found, refer to primitive type */
            String primitive = json.get(Constants.PrimitiveParam).toString();
            String value = json.get(Constants.ValueParam).toString();
            Scanner scanner = new Scanner(value);

            switch (primitive) {
                case "number":
                    if (scanner.hasNextInt()) consumer.accept(scanner.nextInt());
                    if (scanner.hasNextDouble()) consumer.accept(scanner.nextDouble());
                    return;
                case "string":
                    consumer.accept(value);
                    return;
                case "boolean":
                    if (scanner.hasNextBoolean()) consumer.accept(scanner.nextBoolean());
                    return;
            }
        } else if (json.has(Constants.ElementsParam)) {
            if (expectedType == null || !expectedType.isArray()) expectedType = Object[].class;
            Object array = new RestoredArray(json, translator, expectedType).decode();
            consumer.accept(array);
        }
        else if (json.has(Constants.FieldsParam)) {
            RestoredObject restoredObject = new RestoredObject(json, translator);
            Object decoded = restoredObject.decode();
            consumer.accept(decoded);
        } else {
            LOGGER.warn(this.json.toString(2));
            throw new RuntimeException("Unknown JSON encoding");
        }
    }
}