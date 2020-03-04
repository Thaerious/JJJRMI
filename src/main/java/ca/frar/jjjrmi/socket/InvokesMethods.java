package ca.frar.jjjrmi.socket;

public interface InvokesMethods {
    void invokeClientMethod(Object source, String methodName, Object... args);
    public void forget(HasWebsockets aThis);
}
