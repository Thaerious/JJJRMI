package ca.frar.jjjrmi.jsbuilder.code;

import static ca.frar.jjjrmi.Global.LOGGER;
import java.util.HashMap;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtTypeReference;

public class JSAssignment extends AbstractJSCodeElement {
    private final CtAssignment ctAssignment;
    private final JSCodeElement rhs;
    private final JSCodeElement lhs;

    public JSAssignment(CtAssignment ctAssignment) {
        LOGGER.trace(this.getClass().getSimpleName());
        this.ctAssignment = ctAssignment;
        lhs = this.generate(ctAssignment.getAssigned());
        rhs = this.generate(ctAssignment.getAssignment());
    }

    @Override
    public String toString(){
        CtTypeReference<?> lhsType = ctAssignment.getAssigned().getType();
        CtTypeReference<?> rhsType = ctAssignment.getAssignment().getType();
        
        TypeFactory typeFactory = new TypeFactory();
        LOGGER.debug(lhsType + ", " + (lhs == typeFactory.CHARACTER_PRIMITIVE));
        if (lhsType == typeFactory.CHARACTER && rhsType == typeFactory.INTEGER){
             return lhs.toString() + " = String.fromCharCode(" + rhs.toString() + ")";
        } else {        
            return lhs.toString() + " = " + rhs.toString();
        }
    }
    
    public String toXML(int indent) {
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put("name", lhs.toString());
        return toXML(indent, attributes);
    }

}
