package ca.frar.jjjrmi.socket.observer.events;
import javax.websocket.Session;

public class JJJCloseEvent extends JJJEvent{

    public JJJCloseEvent(Session session){
        super(session);
    }
}