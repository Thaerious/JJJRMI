package ca.frar.jjjrmi.socket;

import ca.frar.jjjrmi.annotations.DoNotInvoke;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.translator.HasWebsockets;
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
    synchronized public Collection<InvokesMethods> getWebsockets() {
        return new ArrayList<>(websockets);
    }

    synchronized public void addWebsocket(InvokesMethods socket) {
        this.getWebsockets().add(socket);
    }

    synchronized public void removeWebsocket(InvokesMethods socket) {
        this.getWebsockets().remove(socket);
    }

    synchronized public void forget() {
        this.getWebsockets().forEach(s -> s.forget(this));
    }

    @DoNotInvoke
    synchronized public void invokeClientMethod(String method, Object... args) {
        for (ca.frar.jjjrmi.socket.InvokesMethods invokes : this.getWebsockets()) {
            invokes.invokeClientMethod(this, method, args);
        }
    }
}