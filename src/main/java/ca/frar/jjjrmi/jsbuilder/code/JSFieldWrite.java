package ca.frar.jjjrmi.jsbuilder.code;
import static ca.frar.jjjrmi.jsbuilder.code.JSConstructorCall.LOGGER;
import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Set;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtTypeAccessImpl;

public class JSFieldWrite extends AbstractJSCodeElement {
    private final boolean isStatic;
    private final String type;
    private final String name;
    private JSCodeElement target;
    
    public JSFieldWrite(CtFieldWrite ctFieldWrite) {
        CtFieldReference ctVariable = ctFieldWrite.getVariable();
        CtExpression ctTarget = ctFieldWrite.getTarget();
        
        this.isStatic = ctVariable.isStatic();        
        this.type = ctVariable.getType().getSimpleName();
        this.name = ctVariable.getSimpleName();
        
        if (this.isStatic && ctTarget instanceof CtThisAccess){
            CtTypeAccessImpl ctTypeAccessImpl = new CtTypeAccessImpl();
            CtTypeReference targetType = ctFieldWrite.getTarget().getType();
            ctTypeAccessImpl.setAccessedType(targetType);
            this.target = this.generate(ctTypeAccessImpl);
        } else {
            this.target = this.generate(ctTarget);
        }
    }

    @Override
    public String toString(){
        return this.target.toString() + "." + this.name; 
    }
    
    public String toXML(int indent) {
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put("static", "" + this.isStatic);
        attributes.put("type", "" + this.type);        
        attributes.put("name", "" + this.name);
        return toXML(indent, attributes);
    }    
}