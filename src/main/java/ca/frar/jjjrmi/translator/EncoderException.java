package ca.frar.jjjrmi.translator;

public class EncoderException extends Exception {
    private final Object object;

    public EncoderException(String message, Object object) {
        super(message);
        this.object = object;
    }

    public Object getObject() {
        return object;
    }
}