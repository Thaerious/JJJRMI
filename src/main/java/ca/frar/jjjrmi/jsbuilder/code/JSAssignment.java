package ca.frar.jjjrmi.jsbuilder.code;

import static ca.frar.jjjrmi.Global.LOGGER;
import spoon.reflect.code.CtAssignment;

public class JSAssignment extends AbstractJSCodeElement {
    private final JSCodeElement rhs;
    private final JSCodeElement lhs;

    public JSAssignment(CtAssignment ctAssignment) {
        LOGGER.trace(this.getClass().getSimpleName());
        lhs = this.generate(ctAssignment.getAssigned());
        rhs = this.generate(ctAssignment.getAssignment());
    }

    @Override
    public String toString(){
        return lhs.toString() + " = " + rhs.toString();
    }

}
