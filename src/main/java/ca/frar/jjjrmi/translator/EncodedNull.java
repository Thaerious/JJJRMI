package ca.frar.jjjrmi.translator;
import org.json.JSONObject;

class EncodedNull extends JSONObject{
    EncodedNull(){
        this.put(Constants.TypeParam, Constants.NullValue);
    }
}