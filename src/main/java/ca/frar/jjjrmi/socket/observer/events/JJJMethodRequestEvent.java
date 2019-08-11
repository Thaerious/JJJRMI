package ca.frar.jjjrmi.socket.observer.events;
import ca.frar.jjjrmi.socket.message.MethodRequest;
import javax.websocket.Session;

public class JJJMethodRequestEvent extends JJJEvent{
    private final Object target;
    private final MethodRequest request;

    public JJJMethodRequestEvent(Session session, Object target, MethodRequest request){
        super(session);
        this.target = target;
        this.request = request;
    }

    public Object getTarget() {
        return target;
    }

    public MethodRequest getRequest() {
        return request;
    }
}