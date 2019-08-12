package ca.frar.jjjrmi.socket.message;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.SkipJS;
import ca.frar.jjjrmi.socket.JJJTransientObject;

/**
 * Parent class for messages sent from client to server.
 * @author Ed Armstrong
 */
@JJJ(retain=false)
public class ClientMessage implements JJJTransientObject{
    private ClientMessageType type;

    @SkipJS
    private ClientMessage(){}

    public ClientMessage(ClientMessageType type){
        this.type = type;
    }

    public ClientMessageType getType() {
        return type;
    }
}