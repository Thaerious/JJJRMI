package ca.frar.jjjrmi.translator;

import ca.frar.jjjrmi.exceptions.EncoderException;

public interface EncodeHandler {
    public void setField(String name, Object value) throws EncoderException;
}
