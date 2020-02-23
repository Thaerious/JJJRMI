package ca.frar.jjjrmi.socket.observer.events;
import ca.frar.jjjrmi.rmi.JJJMessage;
import javax.websocket.Session;

public class JJJSendEvent <T extends JJJMessage> extends JJJEvent{
    private final T message;

    public JJJSendEvent(Session session, T message) {
        super(session);
        this.message = message;
    }

    public T getMessage(){
        return message;
    }
}
