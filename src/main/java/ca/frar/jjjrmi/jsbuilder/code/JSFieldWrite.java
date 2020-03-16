package ca.frar.jjjrmi.jsbuilder.code;
import static ca.frar.jjjrmi.jsbuilder.code.JSConstructorCall.LOGGER;
import java.lang.reflect.Member;
import java.util.HashMap;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;

public class JSFieldWrite extends AbstractJSCodeElement {
    private final boolean isStatic;
    private final String type;
    private final String name;
    private final String target;
    
    public JSFieldWrite(CtFieldWrite<?> ctFieldWrite) {
        CtFieldReference ctVariable = ctFieldWrite.getVariable();
        CtExpression ctTarget = ctFieldWrite.getTarget();
        
        this.isStatic = ctVariable.isStatic();        
        this.type = ctVariable.getType().getSimpleName();
        this.name = ctVariable.getSimpleName();
        this.target = ctTarget.getType().toString();
    }

    @Override
    public String toString(){
        return "X";
    }
    
    public String toXML(int indent) {
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put("static", "" + this.isStatic);
        attributes.put("type", "" + this.type);
        attributes.put("name", "" + this.name);
        attributes.put("target", "" + this.target);
        return toXML(indent, attributes);
    }    
}
