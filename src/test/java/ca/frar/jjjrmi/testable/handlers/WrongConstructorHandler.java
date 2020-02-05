package ca.frar.jjjrmi.testable.handlers;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.translator.AHandler;
import ca.frar.jjjrmi.translator.encoder.EncodedResult;
import ca.frar.jjjrmi.translator.Translator;
import java.util.ArrayList;
import org.json.JSONObject;

public class WrongConstructorHandler extends AHandler<ArrayList>{

    WrongConstructorHandler(JSONObject json, EncodedResult encodedResult){
        super(json, encodedResult);
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