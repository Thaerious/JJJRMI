package ca.frar.jjjrmi.translator;
import org.apache.commons.lang3.ClassUtils;

/**
 * Workhorse class for encoding objects.  Used by translator or other encoder
 * objects.
 * @author edward
 */
class Encoder {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(Translator.class);
    private final Object object;
    private final Translator translator;

    Encoder(Object object, Translator translator) {
        this.object = object;
        this.translator = translator;
    }

    EncodedJSON encode() throws IllegalArgumentException, IllegalAccessException, EncoderException {
        LOGGER.trace("Encoder.encode() : " + this.object.getClass().getSimpleName());
        
        if (object == null){
            LOGGER.trace(" -- null");
            return new EncodedNull();
        } else if (ClassUtils.isPrimitiveWrapper(object.getClass()) | object.getClass().isPrimitive() || object.getClass() == String.class) {
            LOGGER.trace(" -- primitive");
            return new EncodedPrimitive(object);
        } else if (translator.hasReferredObject(object)) {
            LOGGER.trace(" -- referred");
            return new EncodedReference(translator.getReference(object));
        } else if (this.object.getClass().isArray()) {
            LOGGER.trace(" -- array");
            return new EncodedArray(object, translator);
        } else if (object.getClass().isEnum()) {
            LOGGER.trace(" -- enum");
            return new EncodedEnum(object);
        } else if (translator.hasHandler(object.getClass())){
            LOGGER.trace(" -- handler");
            EncodedObject encodedObject = new EncodedObject(object, translator);
            Handler handler = translator.getHandler(object.getClass());
            handler.jjjEncode(encodedObject, object);
            return encodedObject;
        } else if (object instanceof SelfHandler){
            LOGGER.trace(" -- selfhandler");
            EncodedObject encodedObject = new EncodedObject(object, translator);
            SelfHandler<?> handler = (SelfHandler) object;
            handler.encode(encodedObject);
            return encodedObject;
        }  else {
            LOGGER.trace(" -- object");
            EncodedObject encodedObject = new EncodedObject(object, translator);
            encodedObject.encode();
            return encodedObject;
        }
    }
}
