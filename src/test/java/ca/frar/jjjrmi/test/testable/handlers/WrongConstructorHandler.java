package ca.frar.jjjrmi.test.testable.handlers;
import java.util.ArrayList;
import ca.frar.jjjrmi.annotations.Handles;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.translator.AHandler;
import ca.frar.jjjrmi.translator.Translator;
import org.json.JSONObject;

public class WrongConstructorHandler extends AHandler<ArrayList>{

    WrongConstructorHandler(JSONObject json, Translator translator){
        super(json, translator);
    }
        
    @Override
    public void jjjEncode(ArrayList list) throws EncoderException {
        Object[] array = list.toArray();
        setField("elementData", array);
    }

    @Override
    public void jjjDecode(ArrayList list) throws DecoderException{
        Object[] array = (Object[]) decodeField("elementData");
        for (int i = 0; i < array.length; i++){
            list.add(array[i]);
        }
    }

    @Override
    public ArrayList<?> instatiate() {
        return new ArrayList<>();
    }
}