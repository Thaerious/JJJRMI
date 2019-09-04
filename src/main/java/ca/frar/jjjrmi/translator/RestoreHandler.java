package ca.frar.jjjrmi.translator;

public interface RestoreHandler {
    public <T> T decodeField(String name) throws DecoderException;
}
