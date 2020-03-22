package ca.frar.jjjrmi.rmi;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.rmi.socket.JJJTransientObject;

/**
 * Parent class for messages sent from client to server.
 * @author Ed Armstrong
 */
@JJJ(retain=false)
public class ClientMessage implements JJJTransientObject{
    private ClientMessageType type;

    private ClientMessage(){}

    public ClientMessage(ClientMessageType type){
        this.type = type;
    }

    public ClientMessageType getType() {
        return type;
    }
}