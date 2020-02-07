package ca.frar.jjjrmi.socket;

import ca.frar.jjjrmi.annotations.DoNotInvoke;
import ca.frar.jjjrmi.socket.InvokesMethods;
import java.util.Collection;

public interface HasWebsockets {

    Collection<InvokesMethods> getWebsockets();

    void addWebsocket(InvokesMethods socket);

    void removeWebsocket(InvokesMethods socket);

    void forget();

    @DoNotInvoke
    void invokeClientMethod(String method, Object... args);
}
