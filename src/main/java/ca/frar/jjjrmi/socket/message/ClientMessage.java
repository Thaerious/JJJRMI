package ca.frar.jjjrmi.socket.message;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.JJJOptions;
import ca.frar.jjjrmi.annotations.SkipJS;
import ca.frar.jjjrmi.translator.DataObject;

/**
 * Parent class for messages sent from client to server.
 * @author Ed Armstrong
 */
@JJJ()
@JJJOptions(retain=false)
public class ClientMessage implements DataObject{
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