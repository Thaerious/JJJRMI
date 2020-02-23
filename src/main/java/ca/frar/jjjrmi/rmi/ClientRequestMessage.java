package ca.frar.jjjrmi.rmi;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.annotations.ProcessLevel;
import static ca.frar.jjjrmi.rmi.JJJMessageType.REMOTE;
import java.util.Arrays;

@JJJ(retain=false)
public class ClientRequestMessage extends JJJMessage{
    private String ptr;
    private String methodName;
    private Object[] args;
    private String uid;

    @NativeJS
    private ClientRequestMessage(){
        super(REMOTE);
    }
    
    /**
    @param uid A unique identifier that the client will return to the server in a @link{InvocationResponse} when the
    task is complete.
    @param ptr Pointer to an object that has previously been sent to the client upon which the method will be called.
    @param methodName The name of the method to call.
    @param args The argument to pass to the method.
    */
    public ClientRequestMessage(String uid, String ptr, String methodName, Object[] args) {
        super(REMOTE);
        this.uid = uid;
        this.ptr = ptr;
        this.methodName = methodName;
        this.args = args;
    }

    String getUid() {
        return uid;
    }
    
   /**
     * @return the ptr
     */
    public String getPtr() {
        return ptr;
    }

    /**
     * @return the methodName
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * @return the args
     */
    public Object[] getArgs() {
        return Arrays.copyOf(args, args.length);
    }    
}