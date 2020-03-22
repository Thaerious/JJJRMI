package ca.frar.jjjrmi.rmi.socket;
import ca.frar.jjjrmi.annotations.DoNotInvoke;
import ca.frar.jjjrmi.annotations.JJJ;
import java.util.Collection;
import java.util.Collections;

@JJJ(generateJS=false)
public interface JJJTransientObject extends HasWebsockets{    
    @Override default Collection<InvokesMethods> getRemoteInvokers() {return Collections.EMPTY_LIST;}
    @Override default void addInvoker(InvokesMethods socket) {}
    @Override default public void removeInvoker(InvokesMethods socket) {}
    @Override @DoNotInvoke default void invokeClientMethod(String method, Object... args) {}
}
