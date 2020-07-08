package ca.frar.jjjrmi.exceptions;

public class JJJRMIKeyException extends DecoderException{

    public JJJRMIKeyException(){
        super("Provided key must be unique");
    }
}
