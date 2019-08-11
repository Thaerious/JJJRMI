package ca.frar.jjjrmi.socket.observer.events;
import java.util.Arrays;
import javax.websocket.Session;

public class JJJMethodInvocationEvent extends JJJEvent{
    private final Object source;
    private final String methodName;
    private final Object[] arguments;

    public JJJMethodInvocationEvent(Session session, Object source, String methodName, Object... args){
        super(session);
        this.source = source;
        this.methodName = methodName;
        this.arguments = args;
    }

    public Object getSource() {
        return source;
    }

    public String getMethodName() {
        return methodName;
    }

    public Object[] getArguments() {
        return Arrays.copyOf(arguments, arguments.length);
    }
}