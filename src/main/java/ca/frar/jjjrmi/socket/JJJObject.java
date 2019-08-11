package ca.frar.jjjrmi.socket;
import ca.frar.jjjrmi.translator.HasWebsockets;
import java.util.ArrayList;
import java.util.Collection;

/**
A class extending this class does not need to be marked up by the RMIParser.  It provides all the methods that the
JJJ Socket requires to interact with the object.
@author edward
*/
public class JJJObject implements HasWebsockets{
    private final ArrayList<InvokesMethods> websockets = new ArrayList<>();

    @Override
    public Collection<InvokesMethods> getWebsockets() {
        return this.websockets;
    }
}