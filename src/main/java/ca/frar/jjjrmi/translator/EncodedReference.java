package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.translator.Constants;
import org.json.JSONObject;

@JJJ(insertJJJMethods=false)
public class EncodedReference extends JSONObject{
    @NativeJS
    public EncodedReference(String ref){
        this.put(Constants.PointerParam, ref);
    }
}