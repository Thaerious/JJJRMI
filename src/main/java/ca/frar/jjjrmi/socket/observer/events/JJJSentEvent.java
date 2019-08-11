package ca.frar.jjjrmi.socket.observer.events;
import ca.frar.jjjrmi.socket.message.JJJMessage;
import ca.frar.jjjrmi.translator.EncodedJSON;
import javax.websocket.Session;

public class JJJSentEvent <T extends JJJMessage> extends JJJEvent{
    private final T message;
    private final EncodedJSON encoded;

    public JJJSentEvent(Session session, T message, EncodedJSON encoded) {
        super(session);
        this.message = message;
        this.encoded = encoded;
    }

    public T getMessage(){
        return message;
    }
    
    public EncodedJSON getEncodedMessage(){
        return encoded;
    }    
}
