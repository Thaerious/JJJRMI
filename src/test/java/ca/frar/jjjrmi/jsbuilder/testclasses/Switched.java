package ca.frar.jjjrmi.jsbuilder.testclasses;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.socket.JJJObject;
import java.io.Serializable;

public class Switched extends JJJObject implements Serializable {
    private static final long serialVersionUID = 1L;
   
    @NativeJS
    public Cardinality cardinality(String target) {
        long a = 1L;
        
        switch (target) {
            case "-0.5 -1.0 0.0":
                return Cardinality.S;
            case "-1.0 0.0 -0.5":
                return Cardinality.NE;
            case "-1.0 -0.5 0.0":
                return Cardinality.SE;
            default:
                return null;
        }
    }
}
