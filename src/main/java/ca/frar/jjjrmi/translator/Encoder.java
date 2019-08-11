package ca.frar.jjjrmi.translator;
import org.apache.commons.lang3.ClassUtils;

public class Encoder {
    private final Object object;
    private final Translator translator;

    public Encoder(Object object, Translator translator) {
        this.object = object;
        this.translator = translator;
    }

    public EncodedJSON encode() throws IllegalArgumentException, IllegalAccessException, EncoderException {
        if (object == null){
            return new EncodedNull();
        } else if (ClassUtils.isPrimitiveWrapper(object.getClass()) | object.getClass().isPrimitive() || object.getClass() == String.class) {
            return new EncodedPrimitive(object);
        } else if (translator.hasReferredObject(object)) {
            return new EncodedReference(translator.getReference(object));
        } else if (this.object.getClass().isArray()) {
            return new EncodedArray(object, translator);
        } else if (object.getClass().isEnum()) {
            return new EncodedEnum(object);
        } else if (translator.hasHandler(object.getClass())){
            EncodedObject encodedObject = new EncodedObject(object, translator);
            Handler handler = translator.getHandler(object.getClass());
            handler.jjjEncode(encodedObject, object);
            return encodedObject;
        } else if (object instanceof SelfHandler){
            EncodedObject encodedObject = new EncodedObject(object, translator);
            SelfHandler<?> handler = (SelfHandler) object;
            handler.encode(encodedObject);
            return encodedObject;
        }  else {
            EncodedObject encodedObject = new EncodedObject(object, translator);
            encodedObject.encode();
            return encodedObject;
        }
    }
}
