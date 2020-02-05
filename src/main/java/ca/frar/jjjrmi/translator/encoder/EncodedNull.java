package ca.frar.jjjrmi.translator.encoder;

import ca.frar.jjjrmi.translator.Constants;
import org.json.JSONObject;

public class EncodedNull extends JSONObject{
    public EncodedNull(){
        this.put(Constants.TypeParam, Constants.NullValue);
    }
}