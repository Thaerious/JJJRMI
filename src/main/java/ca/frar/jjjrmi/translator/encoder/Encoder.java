package ca.frar.jjjrmi.translator.encoder;

import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.exceptions.TranslatorException;
import ca.frar.jjjrmi.translator.Constants;
import ca.frar.jjjrmi.translator.HandlerFactory;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.ClassUtils;
import org.json.JSONObject;

/**
 * Workhorse class for encoding objects. Used by translator or other encoder
 * objects.
 *
 * @author edward
 */
@JJJ(insertJJJMethods = false)
public class Encoder {

    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JJJRMI");
    private final Object object;
    private final EncodedResult encodedResult;

    @NativeJS
    public Encoder(Object object, EncodedResult encodedResult) {
        this.object = object;
        this.encodedResult = encodedResult;
    }

    @NativeJS
    public JSONObject encode() throws EncoderException {
        try {
            if (this.object != null) LOGGER.trace("Encoder.encode() : " + this.object.getClass().getSimpleName());
            else LOGGER.trace("Encoder.encode() : null");

            if (object == null) {
                LOGGER.trace(" -- null");
                return new EncodedNull();
            } else if (ClassUtils.isPrimitiveWrapper(object.getClass()) | object.getClass().isPrimitive() || object.getClass() == String.class) {
                LOGGER.trace(" -- primitive");
                return new EncodedPrimitive(object);
            } else if (encodedResult.getTranslator().hasReferredObject(object)) {
                LOGGER.trace(" -- referred");
                return new EncodedReference(encodedResult.getTranslator().getReference(object));
            } else if (this.object.getClass().isArray()) {
                LOGGER.trace(" -- array");
                return new EncodedArray(object, encodedResult);
            } else if (object.getClass().isEnum()) {
                LOGGER.trace(" -- enum");
                return new EncodedEnum(object);
            } else if (HandlerFactory.getInstance().hasHandler(object.getClass())) {
                LOGGER.trace(" -- handler");
                Class<? extends AHandler<?>> handlerClass = HandlerFactory.getInstance().getHandler(object.getClass());
                AHandler<?> handler = handlerClass.getConstructor(EncodedResult.class).newInstance(this.encodedResult);
                EncodedObject encodedObject = handler.doEncode(object);
                encodedResult.put(encodedObject);
                return new EncodedReference(encodedResult.getTranslator().getReference(object));
            } else {
                LOGGER.trace(" -- object");
                try {
                    EncodedObject encodedObject = new EncodedObject(object, this.encodedResult);
                    encodedResult.put(encodedObject);
                    encodedObject.encode();
                    return new EncodedReference(encodedResult.getTranslator().getReference(object));
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    throw new EncoderException(ex, object);
                }
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new EncoderException(ex, object);
        }
    }
}
