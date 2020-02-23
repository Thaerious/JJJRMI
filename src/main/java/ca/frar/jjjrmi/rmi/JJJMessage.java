package ca.frar.jjjrmi.rmi;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.socket.JJJTransientObject;

@JJJ(retain=false)
public abstract class JJJMessage implements JJJTransientObject{
    protected JJJMessageType type;
    protected JJJMessage(){}

    JJJMessage(JJJMessageType type){
        this.type = type;
    }

    public JJJMessageType getType(){
        return type;
    }
}
