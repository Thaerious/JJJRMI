package ca.frar.jjjrmi.rmi.socket;

public interface InvokesMethods {
    void invokeClientMethod(Object source, String methodName, Object... args);
}
