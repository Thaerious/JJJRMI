package ca.frar.jjjrmi.socket.observer.events;
import ca.frar.jjjrmi.socket.message.JJJMessage;
import ca.frar.jjjrmi.translator.encoder.EncodedResult;
import javax.websocket.Session;

public class JJJSentEvent <T extends JJJMessage> extends JJJEvent{
    private final T message;
    private final EncodedResult encoded;

    public JJJSentEvent(Session session, T message, EncodedResult encoded) {
        super(session);
        this.message = message;
        this.encoded = encoded;
    }

    public T getMessage(){
        return message;
    }
    
    public EncodedResult getEncodedMessage(){
        return encoded;
    }    
}
