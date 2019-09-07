package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.exceptions.DecoderException;

public interface RestoreHandler {
    public <T> T decodeField(String name) throws DecoderException;
}
