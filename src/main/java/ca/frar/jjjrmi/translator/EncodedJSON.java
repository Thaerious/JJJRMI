package ca.frar.jjjrmi.translator;
import org.json.JSONObject;

public class EncodedJSON extends JSONObject{
    protected Translator translator;

    protected EncodedJSON(Translator translator){
        super();
        this.translator = translator;
    }

    EncodedJSON(Translator translator, String jsonString) {
        super(jsonString);
        this.translator = translator;
    }
    
    public void setRetain(boolean value){
        this.put(Constants.RetainParam, value);
    }
}
