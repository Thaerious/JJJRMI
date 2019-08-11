package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.annotations.DoNotInvoke;
import ca.frar.jjjrmi.socket.InvokesMethods;
import java.util.Collection;

public interface HasWebsockets {
    Collection<InvokesMethods> getWebsockets();

    default void addWebsocket(InvokesMethods socket) {
        this.getWebsockets().add(socket);
    }

    default void removeWebsocket(InvokesMethods socket) {
        this.getWebsockets().remove(socket);
    }

    default void forget() {
        this.getWebsockets().forEach(s->s.forget(this));
    }    
    
    @DoNotInvoke
    default void invokeClientMethod(String method, Object ... args){
        for (ca.frar.jjjrmi.socket.InvokesMethods invokes : this.getWebsockets()){
            invokes.invokeClientMethod(this, method, args);
        }
    }
}
