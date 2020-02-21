package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.translator.Constants;
import org.json.JSONObject;

@JJJ(insertJJJMethods=false)
class EncodedNull extends JSONObject{
    @NativeJS
    EncodedNull(){
        this.put(Constants.TypeParam, Constants.NullValue);
    }
}