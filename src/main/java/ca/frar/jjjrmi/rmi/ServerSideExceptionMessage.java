package ca.frar.jjjrmi.rmi;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.annotations.Transient;
import static ca.frar.jjjrmi.rmi.JJJMessageType.EXCEPTION;

/**
 * Message from server to client to indicate an uncaught exception has occurred.
 * @author Ed Armstrong
 */
@JJJ(retain=false)
public final class ServerSideExceptionMessage extends JJJMessage{  
    @Transient public final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(ServerSideExceptionMessage.class);
    private String exception;
    private String message;
    private String uid;
    private String methodName;
    private String objectPTR;
    @Transient Throwable ex;

    @NativeJS
    private ServerSideExceptionMessage(){
        super(EXCEPTION);
    }
    
    public ServerSideExceptionMessage(Throwable ex){
        super(EXCEPTION);
        this.uid = "";
        this.objectPTR = "";
        this.methodName = "";
        this.exception = ex.getClass().getSimpleName();
        this.message = ex.getMessage();
        this.ex = ex;
    }

    public ServerSideExceptionMessage(String uid, String objectPTR, String methodName, Throwable ex){
        super(EXCEPTION);
        this.uid = uid;
        this.objectPTR = objectPTR;
        this.methodName = methodName;
        this.exception = ex.getClass().getSimpleName();
        this.message = ex.getMessage();
        this.ex = ex;
    }

    public ServerSideExceptionMessage(String uid, String objectPTR, String methodName, String exception, String message){
        super(EXCEPTION);
        this.uid = uid;
        this.objectPTR = objectPTR;
        this.methodName = methodName;
        this.exception = exception;
        this.message = message;
    }

    public Throwable getThrowable() {
        return this.ex;
    }
    
    public String getMessage() {
        return this.message;
    }    
}
