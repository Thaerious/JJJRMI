package ca.frar.jjjrmi.socket;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.socket.InvokesMethods;
import ca.frar.jjjrmi.translator.HasWebsockets;
import java.util.Collection;
import java.util.Collections;

@JJJ(generateJS=false)
public interface JJJTransientObject extends HasWebsockets{
    @Override default Collection<InvokesMethods> getWebsockets() {return Collections.EMPTY_LIST;}
    @Override default void addWebsocket(InvokesMethods socket) {};
    @Override default public void removeWebsocket(InvokesMethods socket) {};
    @Override default public void forget() {};    
}