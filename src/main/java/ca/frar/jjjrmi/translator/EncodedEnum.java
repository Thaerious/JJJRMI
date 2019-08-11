package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.utility.JJJOptionsHandler;

public class EncodedEnum extends EncodedJSON{
    public EncodedEnum(Object value) throws EncoderException{
        super(null);

        JJJOptionsHandler jjjOptions = new JJJOptionsHandler(value);
        if (!jjjOptions.hasJJJ()){
            String message = String.format("Attempt to encode enum '%s' without @JJJ annotation", value.getClass().getSimpleName());
            throw new EncoderException(message, value);
        }

        this.put(Constants.EnumParam, value.getClass().getName());
        this.put(Constants.ValueParam, value.toString());
    }
}