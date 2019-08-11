package ca.frar.jjjrmi.translator;
import org.json.JSONObject;

public interface EncodeHandler {
    public void setField(String name, Object value) throws IllegalArgumentException, IllegalAccessException, EncoderException;
}
