package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.translator.Constants;
import org.json.JSONObject;

@JJJ(insertJJJMethods=false)
class EncodedReference extends JSONObject{
    @NativeJS
    EncodedReference(String ref){
        this.put(Constants.PointerParam, ref);
    }
}