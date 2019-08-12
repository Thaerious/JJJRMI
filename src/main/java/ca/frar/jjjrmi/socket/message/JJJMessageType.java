package ca.frar.jjjrmi.socket.message;
import ca.frar.jjjrmi.annotations.JJJ;

@JJJ(retain=false)
public enum JJJMessageType {
    LOCAL,
    REMOTE,
    READY,
    LOAD,
    EXCEPTION,
    FORGET,
    REJECTED_CONNECTION
}
