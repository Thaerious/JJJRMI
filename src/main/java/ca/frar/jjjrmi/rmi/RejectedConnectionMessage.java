package ca.frar.jjjrmi.rmi;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;
import static ca.frar.jjjrmi.rmi.JJJMessageType.REJECTED_CONNECTION;

/**
 * Message sent from server to client to indicate a connect request was 
 * rejected.
 * @author Ed Armstrong
 */
@JJJ(retain=false)
public class RejectedConnectionMessage extends JJJMessage{

    @NativeJS
    public RejectedConnectionMessage(){
        super(REJECTED_CONNECTION);
    }

}
