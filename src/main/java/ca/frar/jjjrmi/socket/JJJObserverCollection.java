package ca.frar.jjjrmi.socket;
import ca.frar.jjjrmi.socket.message.JJJMessage;
import ca.frar.jjjrmi.socket.observer.*;
import ca.frar.jjjrmi.socket.observer.events.*;
import java.util.ArrayList;

final class JJJObserverCollection extends ArrayList<JJJObserver>{
    void open(JJJOpenEvent event){
        for (JJJObserver o : this){
            o.socketOpen(event);
            if (event.isPropgationStopped()) break;
        }
    }
    void receive(JJJReceiveEvent event){
        for (JJJObserver o : this){
            o.socketReceive(event);
            if (event.isPropgationStopped()) break;
        }
    }
    void send(JJJSendEvent<?> event){
        for (JJJObserver o : this){
            o.socketSend(event);
            if (event.isPropgationStopped()) break;
        }
    }
    void clientMethodInvocation(JJJMethodInvocationEvent event){
        for (JJJObserver o : this){
            o.clientMethodInvocation(event);
            if (event.isPropgationStopped()) break;
        }
    }
    void exception(JJJExceptionEvent event){
        for (JJJObserver o : this){
            o.socketException(event);
            if (event.isPropgationStopped()) break;
        }
    }
    void serverMethodRequest(JJJMethodRequestEvent event){
        for (JJJObserver o : this){
            o.serverMethodRequest(event);
            if (event.isPropgationStopped()) break;
        }
    }
    void close(JJJCloseEvent event){
        for (JJJObserver o : this){
            o.socketClose(event);
            if (event.isPropgationStopped()) break;
        }
    }
    void sent(JJJSentEvent<? extends JJJMessage> event) {
        for (JJJObserver o : this){
            o.socketSent(event);
            if (event.isPropgationStopped()) break;
        }
    }
}
