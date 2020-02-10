package ca.frar.jjjrmi.translator.encoder;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.translator.Constants;
import org.json.JSONObject;

@JJJ(insertJJJMethods=false)
public class EncodedNull extends JSONObject{
    @NativeJS
    public EncodedNull(){
        this.put(Constants.TypeParam, Constants.NullValue);
    }
}