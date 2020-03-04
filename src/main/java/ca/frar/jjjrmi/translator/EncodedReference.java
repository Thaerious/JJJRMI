package ca.frar.jjjrmi.translator;
import org.json.JSONObject;

class EncodedReference extends JSONObject{
    EncodedReference(String ref){
        this.put(Constants.PointerParam, ref);
    }
}