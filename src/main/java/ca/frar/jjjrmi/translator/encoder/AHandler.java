package ca.frar.jjjrmi.translator.encoder;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.EncoderException;
import org.json.JSONObject;

/**
 *
 * @author Ed Armstrong
 */
abstract public class AHandler<T> {
    private final EncodedResult encodedResult;
    private EncodedFields fields;
    private JSONObject jsonObject;

    public AHandler(EncodedResult encodedResult){
        this.encodedResult = encodedResult;        
    }
    
    public final EncodedObject doEncode(Object object) throws EncoderException{
        EncodedObject encodedObject = new EncodedObject(object, encodedResult);
        this.fields = encodedObject.fields;
        this.jsonObject = encodedObject;        
        this.encode((T) object);
        return encodedObject;
    }
    
    public final T doDecode(JSONObject json) throws DecoderException{
        this.jsonObject = json;        
        return this.decode();
    }

    abstract public T decode() throws DecoderException;

    abstract public void encode(T object) throws EncoderException;

    public <T> T decodeField(String name) throws DecoderException {        
        return (T) this.encodedResult.getTranslator().decode(fields.getJSONObject(name).toString());
    }

    /**
     * Will encode 'value' and set 'field' to that value.
     * @param name
     * @param value
     * @throws EncoderException 
     */
    public void setField(String field, Object value) throws EncoderException {
        JSONObject toJSON = new Encoder(value, this.encodedResult).encode();
        this.fields.put(field, toJSON);
    }
    
}
