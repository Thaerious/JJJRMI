package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.exceptions.EncoderException;
import org.json.JSONObject;

class EncodedEnum extends JSONObject{
    EncodedEnum(Object value) throws EncoderException{
        this.put(Constants.EnumParam, value.getClass().getName());
        this.put(Constants.ValueParam, value.toString());
    }
}