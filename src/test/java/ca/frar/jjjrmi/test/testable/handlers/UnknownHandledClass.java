package ca.frar.jjjrmi.test.testable.handlers;
import java.util.ArrayList;
import ca.frar.jjjrmi.annotations.Handles;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.translator.AHandler;
import ca.frar.jjjrmi.translator.Translator;
import org.json.JSONObject;

@Handles("not.a.class")
public class UnknownHandledClass extends AHandler<ArrayList>{

    public UnknownHandledClass(JSONObject json, Translator translator){
        super(json, translator);
    }
        
    @Override
    public void jjjEncode(ArrayList list) throws EncoderException {
    }

    @Override
    public void jjjDecode(ArrayList list) throws DecoderException{
    }

    @Override
    public ArrayList<?> instatiate() {
        return null;
    }
}