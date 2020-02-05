package ca.frar.jjjrmi.translator.encoder;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.translator.Constants;
import org.json.JSONObject;

public class EncodedEnum extends JSONObject{
    public EncodedEnum(Object value) throws EncoderException{
        this.put(Constants.EnumParam, value.getClass().getName());
        this.put(Constants.ValueParam, value.toString());
    }
}