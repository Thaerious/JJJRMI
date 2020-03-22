package ca.frar.jjjrmi.rmi.socket;

import ca.frar.jjjrmi.annotations.DoNotInvoke;
import ca.frar.jjjrmi.annotations.JJJ;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Provides all the methods that the JJJ Socket requires to interact with the
 * object.
 *
 * @author edward
 */
@JJJ(generateJS = false)
public class JJJObject implements HasWebsockets {
    private final ArrayList<InvokesMethods> websockets = new ArrayList<>();

    @Override
    synchronized public Collection<InvokesMethods> getRemoteInvokers() {
        return new ArrayList<>(websockets);
    }

    synchronized public void addInvoker(InvokesMethods socket) {
        this.websockets.add(socket);
    }

    synchronized public void removeInvoker(InvokesMethods socket) {
        this.websockets.remove(socket);
    }

    @DoNotInvoke
    synchronized public void invokeClientMethod(String method, Object... args) {
        for (ca.frar.jjjrmi.rmi.socket.InvokesMethods invokes : this.getRemoteInvokers()) {
            invokes.invokeClientMethod(this, method, args);
        }
    }
}