package ca.frar.jjjrmi.socket.observer.events;
import ca.frar.jjjrmi.translator.Translator;
import javax.websocket.Session;

public class JJJOpenEvent extends JJJEvent{

    private final Translator translator;
    public boolean rejected = false;

    public JJJOpenEvent(Session session, Translator translator){
        super(session);
        this.translator = translator;
    }

    /**
    Prevent default actions and send client a reject message.
    */
    public void rejectConnection(){
        this.rejected = true;
    }

    public boolean isRejected(){
        return this.rejected;
    }

    public Translator getTranslator() {
        return translator;
    }
}