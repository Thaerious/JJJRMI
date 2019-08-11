package ca.frar.jjjrmi.socket.message;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.JJJOptions;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.annotations.ProcessLevel;
import static ca.frar.jjjrmi.socket.message.JJJMessageType.FORGET;
import static ca.frar.jjjrmi.socket.message.JJJMessageType.LOCAL;

/**
 * Message from server to client to send a response to MethodRequest.
 * @author Ed Armstrong
 */
@JJJ()
@JJJOptions(retain=false, processLevel=ProcessLevel.ALL)
public final class MethodResponse extends JJJMessage{

    private String uid;
    private String methodName;
    private String objectPTR;
    private Object rvalue;

    @NativeJS
    private MethodResponse(){
        super(LOCAL);
    }    
    
    public String getUid() {
        return uid;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getObjectPTR() {
        return objectPTR;
    }

    public Object getRvalue() {
        return rvalue;
    }

    public MethodResponse(String uid, String objectPTR, String methodName, Object rvalue) {
        super(LOCAL);
        this.uid = uid;
        this.methodName = methodName;
        this.rvalue = rvalue;
        this.objectPTR = objectPTR;
    }
}
