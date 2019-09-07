package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.exceptions.EncoderException;

public class EncodedEnum extends EncodedJSON{
    public EncodedEnum(Object value) throws EncoderException{
        super(null);
        this.put(Constants.EnumParam, value.getClass().getName());
        this.put(Constants.ValueParam, value.toString());
    }
}