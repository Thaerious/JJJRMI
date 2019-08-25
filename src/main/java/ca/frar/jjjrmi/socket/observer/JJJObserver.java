package ca.frar.jjjrmi.socket.observer;
import ca.frar.jjjrmi.socket.observer.events.JJJSendEvent;
import ca.frar.jjjrmi.socket.observer.events.JJJOpenEvent;
import ca.frar.jjjrmi.socket.observer.events.JJJCloseEvent;
import ca.frar.jjjrmi.socket.observer.events.JJJExceptionEvent;
import ca.frar.jjjrmi.socket.observer.events.JJJHandshakeEvent;
import ca.frar.jjjrmi.socket.observer.events.JJJMethodInvocationEvent;
import ca.frar.jjjrmi.socket.observer.events.JJJMethodRequestEvent;
import ca.frar.jjjrmi.socket.observer.events.JJJReceiveEvent;
import ca.frar.jjjrmi.socket.observer.events.JJJSentEvent;

public interface JJJObserver {
     void socketOpen(JJJOpenEvent event);
     void socketReceive(JJJReceiveEvent event);
     void socketSend(JJJSendEvent<?> event);
     void clientMethodInvocation(JJJMethodInvocationEvent event);
     void socketException(JJJExceptionEvent event);
     void serverMethodRequest(JJJMethodRequestEvent event);
     void socketClose(JJJCloseEvent event);
     void socketSent(JJJSentEvent<?> event);
     void handshake(JJJHandshakeEvent event);
}