package ca.frar.jjjrmi.socket;
import ca.frar.jjjrmi.annotations.DoNotInvoke;
import ca.frar.jjjrmi.annotations.JJJ;
import java.util.Collection;
import java.util.Collections;

@JJJ(generateJS=false)
public interface JJJTransientObject extends HasWebsockets{    
    @Override default Collection<InvokesMethods> getWebsockets() {return Collections.EMPTY_LIST;}
    @Override default void addWebsocket(InvokesMethods socket) {}
    @Override default public void removeWebsocket(InvokesMethods socket) {}
    @Override default public void forget() {}
    @Override @DoNotInvoke default void invokeClientMethod(String method, Object... args) {}
}
