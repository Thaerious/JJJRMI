package ca.frar.jjjrmi.translator.encoder;

import ca.frar.jjjrmi.translator.Constants;
import org.json.JSONObject;

public class EncodedReference extends JSONObject{
    public EncodedReference(String ref){
        this.put(Constants.PointerParam, ref);
    }
}