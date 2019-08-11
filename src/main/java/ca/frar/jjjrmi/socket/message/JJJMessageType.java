package ca.frar.jjjrmi.socket.message;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.JJJOptions;

@JJJ()
@JJJOptions(retain=false)
public enum JJJMessageType {
    LOCAL,
    REMOTE,
    READY,
    LOAD,
    EXCEPTION,
    FORGET,
    REJECTED_CONNECTION
}
