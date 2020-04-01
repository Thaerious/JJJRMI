package ca.frar.jjjrmi.jsbuilder.code;

import ca.frar.jjjrmi.jsbuilder.JSCodeElement;
import ca.frar.jjjrmi.jsbuilder.AbstractJSCodeElement;
import static ca.frar.jjjrmi.Global.LOGGER;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtOperatorAssignment;

public class JSOperatorAssignment extends AbstractJSCodeElement {

    private final BinaryOperatorKind kind;
    private final JSCodeElement rhs;
    private final JSCodeElement lhs;

    public JSOperatorAssignment(CtOperatorAssignment ctOperatorAssignment) {
        LOGGER.trace(this.getClass().getSimpleName());
        lhs = this.generate(ctOperatorAssignment.getAssigned());
        rhs = this.generate(ctOperatorAssignment.getAssignment());
        kind = ctOperatorAssignment.getKind();
    }

@Override
    public String toString() {
        return lhs.toString() + " " + CodeFactory.binaryOperator(kind) + "= " + rhs.toString();
    }
}
