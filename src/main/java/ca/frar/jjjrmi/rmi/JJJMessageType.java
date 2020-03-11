package ca.frar.jjjrmi.rmi;
import ca.frar.jjjrmi.annotations.JJJ;

@JJJ(retain=false)
public enum JJJMessageType {
    LOCAL,
    REMOTE,
    READY,
    LOAD,
    EXCEPTION,
    REJECTED_CONNECTION
}
