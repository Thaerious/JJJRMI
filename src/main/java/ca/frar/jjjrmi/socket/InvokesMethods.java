package ca.frar.jjjrmi.socket;

import ca.frar.jjjrmi.exceptions.JJJRMIException;
import java.io.IOException;

public interface InvokesMethods {
    void invokeClientMethod(Object source, String methodName, Object... args);
    public void forget(HasWebsockets aThis);
}
