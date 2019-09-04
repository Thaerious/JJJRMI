package ca.frar.jjjrmi.translator;

public interface EncodeHandler {
    public void setField(String name, Object value) throws IllegalArgumentException, IllegalAccessException, EncoderException;
}
