package ca.frar.jjjrmi.socket.observer;
import ca.frar.jjjrmi.socket.observer.events.JJJSendEvent;
import ca.frar.jjjrmi.socket.observer.events.JJJOpenEvent;
import ca.frar.jjjrmi.socket.observer.events.JJJCloseEvent;
import ca.frar.jjjrmi.socket.observer.events.JJJExceptionEvent;
import ca.frar.jjjrmi.socket.observer.events.JJJMethodInvocationEvent;
import ca.frar.jjjrmi.socket.observer.events.JJJMethodRequestEvent;
import ca.frar.jjjrmi.socket.observer.events.JJJReceiveEvent;
import ca.frar.jjjrmi.socket.observer.events.JJJSentEvent;

public interface JJJObserver {
    default void socketOpen(JJJOpenEvent event) {}
    default void socketReceive(JJJReceiveEvent event) {}
    default void socketSend(JJJSendEvent<?> event) {}
    default void clientMethodInvocation(JJJMethodInvocationEvent event) {}
    default void socketException(JJJExceptionEvent event) {}
    default void serverMethodRequest(JJJMethodRequestEvent event) {}
    default void socketClose(JJJCloseEvent event) {}
    default void socketSent(JJJSentEvent<?> event) {}
}