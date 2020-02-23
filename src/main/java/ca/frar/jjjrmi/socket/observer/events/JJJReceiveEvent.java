package ca.frar.jjjrmi.socket.observer.events;
import ca.frar.jjjrmi.rmi.ClientMessage;
import javax.websocket.Session;

public class JJJReceiveEvent extends JJJEvent{
    private final ClientMessage message;
    private final String text;
    public boolean rejected = false;

    public JJJReceiveEvent(Session session, ClientMessage message, String text){
        super(session);
        this.message = message;
        this.text = text;
    }

    public ClientMessage getMessage() {
        return message;
    }

    public String getText() {
        return text;
    }
}