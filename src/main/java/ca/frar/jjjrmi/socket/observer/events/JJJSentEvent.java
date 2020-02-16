package ca.frar.jjjrmi.socket.observer.events;
import ca.frar.jjjrmi.socket.message.JJJMessage;
import ca.frar.jjjrmi.translator.TranslatorResult;
import javax.websocket.Session;

public class JJJSentEvent <T extends JJJMessage> extends JJJEvent{
    private final T message;
    private final TranslatorResult encoded;

    public JJJSentEvent(Session session, T message, TranslatorResult encoded) {
        super(session);
        this.message = message;
        this.encoded = encoded;
    }

    public T getMessage(){
        return message;
    }
    
    public TranslatorResult getEncodedMessage(){
        return encoded;
    }    
}
