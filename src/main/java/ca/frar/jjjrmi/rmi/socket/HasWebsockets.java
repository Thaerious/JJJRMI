package ca.frar.jjjrmi.rmi.socket;

import ca.frar.jjjrmi.annotations.DoNotInvoke;
import ca.frar.jjjrmi.rmi.socket.InvokesMethods;
import java.util.Collection;

public interface HasWebsockets {

    Collection<InvokesMethods> getRemoteInvokers();

    void addInvoker(InvokesMethods socket);

    void removeInvoker(InvokesMethods socket);

    @DoNotInvoke
    void invokeClientMethod(String method, Object... args);
}
