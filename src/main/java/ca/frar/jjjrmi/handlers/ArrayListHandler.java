package ca.frar.jjjrmi.handlers;
import ca.frar.jjjrmi.translator.DecoderException;
import ca.frar.jjjrmi.translator.EncodeHandler;
import ca.frar.jjjrmi.translator.Handler;
import ca.frar.jjjrmi.translator.EncoderException;
import ca.frar.jjjrmi.translator.RestoreHandler;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class ArrayListHandler implements Handler <ArrayList>{

    @Override
    public void jjjEncode(EncodeHandler handler, ArrayList list) throws IllegalArgumentException, IllegalAccessException, EncoderException {
        Object[] array = list.toArray();
        handler.setField("elementData", array);
    }

    @Override
    public void jjjDecode(RestoreHandler handler, ArrayList list) throws DecoderException{

        Object[] array = (Object[]) handler.decodeField("elementData");
        for (int i = 0; i < array.length; i++){
            list.add(array[i]);
        }
    }

    @Override
    public ArrayList<?> instatiate() {
        return new ArrayList<>();
    }
}