package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.socket.InvokesMethods;
import java.util.Collection;
import java.util.Collections;

public interface DataObject extends HasWebsockets{
    @Override default Collection<InvokesMethods> getWebsockets() {return Collections.EMPTY_LIST;}
    @Override default void addWebsocket(InvokesMethods socket) {};
    @Override default public void removeWebsocket(InvokesMethods socket) {};
    @Override default public void forget() {};    
}
