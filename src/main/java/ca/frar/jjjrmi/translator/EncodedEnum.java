package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.translator.Constants;
import org.json.JSONObject;

@JJJ(insertJJJMethods=false)
class EncodedEnum extends JSONObject{
    @NativeJS
    EncodedEnum(Object value) throws EncoderException{
        this.put(Constants.EnumParam, value.getClass().getName());
        this.put(Constants.ValueParam, value.toString());
    }
}