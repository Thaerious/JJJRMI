package ca.frar.jjjrmi.rmi.socket;

public class InvalidJJJSessionException extends Exception{

    public InvalidJJJSessionException(){
        super();
    }

    public InvalidJJJSessionException(String message){
        super(message);
    }
}