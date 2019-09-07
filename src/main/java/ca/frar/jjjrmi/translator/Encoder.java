package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.exceptions.TranslatorException;
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

    EncodedJSON encode() throws EncoderException {
        if (this.object != null) LOGGER.trace("Encoder.encode() : " + this.object.getClass().getSimpleName());
        else LOGGER.trace("Encoder.encode() : null");
        
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
            try {
                EncodedObject encodedObject = new EncodedObject(object, translator);            
                AHandler handler;                
                handler = translator.newHandler(object.getClass(), encodedObject);
                encodedObject.put(Constants.HandlerParam, handler.getClass().getCanonicalName());
                handler.jjjEncode(object);
                return encodedObject;
            } catch (TranslatorException ex) {
                throw new EncoderException(ex, object);
            }                        
        } else {
            LOGGER.trace(" -- object");            
            try {
                EncodedObject encodedObject = new EncodedObject(object, translator);
                encodedObject.encode();
                return encodedObject;
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new EncoderException(ex, object);
            }            
        }
    }
}
