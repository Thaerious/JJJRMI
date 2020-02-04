package ca.frar.jjjrmi.jsbuilder.code.extend;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.socket.JJJObject;

public class SelfRef extends JJJObject{

    @NativeJS
    public SelfRef copy(){
        return new SelfRef();
    }

}