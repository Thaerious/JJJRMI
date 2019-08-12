package ca.frar.jjjrmi.socket.observer;
import ca.frar.jjjrmi.socket.observer.events.JJJCloseEvent;
import ca.frar.jjjrmi.socket.observer.events.JJJExceptionEvent;
import ca.frar.jjjrmi.socket.observer.events.JJJMethodInvocationEvent;
import ca.frar.jjjrmi.socket.observer.events.JJJMethodRequestEvent;
import ca.frar.jjjrmi.socket.observer.events.JJJOpenEvent;
import ca.frar.jjjrmi.socket.observer.events.JJJReceiveEvent;
import ca.frar.jjjrmi.socket.observer.events.JJJSendEvent;
import ca.frar.jjjrmi.socket.observer.events.JJJSentEvent;

public class JJJObserverImpl implements JJJObserver{
    @Override
    public void socketOpen(JJJOpenEvent event) {    }

    @Override
    public void socketReceive(JJJReceiveEvent event) {    }

    @Override
    public void socketSend(JJJSendEvent<?> event) {    }

    @Override
    public void clientMethodInvocation(JJJMethodInvocationEvent event) {    }

    @Override
    public void socketException(JJJExceptionEvent event) {    }

    @Override
    public void serverMethodRequest(JJJMethodRequestEvent event) {    }

    @Override
    public void socketClose(JJJCloseEvent event) {    }
    
    @Override
    public void socketSent(JJJSentEvent<?> event) {    }
}