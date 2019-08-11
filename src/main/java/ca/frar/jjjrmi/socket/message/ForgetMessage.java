package ca.frar.jjjrmi.socket.message;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.JJJOptions;
import ca.frar.jjjrmi.annotations.NativeJS;
import static ca.frar.jjjrmi.socket.message.JJJMessageType.FORGET;

/**
 * Message from server to client indicating a reference should be cleared.
 * @author Ed Armstrong
 */

@JJJ()
@JJJOptions(retain=false)
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
