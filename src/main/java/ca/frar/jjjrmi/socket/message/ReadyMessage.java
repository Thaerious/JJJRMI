package ca.frar.jjjrmi.socket.message;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;
import static ca.frar.jjjrmi.socket.message.JJJMessageType.READY;

/**
 * Message sent from server to client containing the root object.
 * @author Ed Armstrong
 * @param <T> 
 */

@JJJ(retain=false)
public class ReadyMessage <T> extends JJJMessage{
    private T root;
    
    @NativeJS
    private ReadyMessage(){
        super(READY);
    }
    
    public ReadyMessage(T t){
        super(READY);
        this.root = t;
    }

    @NativeJS
    public T getRoot(){
        return root;
    }

}
