package ca.frar.jjjrmi.socket.observer.events;

import javax.websocket.Session;

public class JJJExceptionEvent extends JJJEvent {
    private final Throwable exception;

    public JJJExceptionEvent(Session session, Throwable exception) {
        super(session);
        this.exception = exception;
    }

    public Throwable getException(){
        return exception;
    }
}
