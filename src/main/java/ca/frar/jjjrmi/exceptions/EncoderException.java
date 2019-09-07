package ca.frar.jjjrmi.exceptions;

public class EncoderException extends JJJRMIException {
    private final Object object;

    public EncoderException(String message, Object object) {
        super(message);
        this.object = object;
    }

    public EncoderException(Exception exception, Object object) {
        super(exception);
        this.object = object;
    }    
    
    public Object getObject() {
        return object;
    }
}