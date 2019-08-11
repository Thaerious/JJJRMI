package ca.frar.jjjrmi.socket.message;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.JJJOptions;
import ca.frar.jjjrmi.translator.DataObject;

@JJJ()
@JJJOptions(retain=false)
public abstract class JJJMessage implements DataObject{
    protected JJJMessageType type;
    protected JJJMessage(){}

    JJJMessage(JJJMessageType type){
        this.type = type;
    }

    public JJJMessageType getType(){
        return type;
    }
}
