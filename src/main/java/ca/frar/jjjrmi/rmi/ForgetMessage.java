package ca.frar.jjjrmi.rmi;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;
import static ca.frar.jjjrmi.rmi.JJJMessageType.FORGET;

/**
 * Message from server to client indicating a reference should be cleared.
 * @author Ed Armstrong
 */

@JJJ(retain=false)
public final class ForgetMessage extends JJJMessage{
    private String key;

    @NativeJS
    private ForgetMessage(){
        super(FORGET);
    }

    public ForgetMessage(String key){
        super(FORGET);
        this.key = key;
    }
}