package ca.frar.jjjrmi.socket;

public class InvalidJJJSessionException extends Exception{

    public InvalidJJJSessionException(){
        super();
    }

    public InvalidJJJSessionException(String message){
        super(message);
    }
}