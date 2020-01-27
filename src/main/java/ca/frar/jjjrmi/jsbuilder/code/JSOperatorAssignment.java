package ca.frar.jjjrmi.jsbuilder.code;

import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtOperatorAssignment;

public class JSOperatorAssignment extends AbstractJSCodeElement {

    private final BinaryOperatorKind kind;
    private final JSCodeElement rhs;
    private final JSCodeElement lhs;

    public JSOperatorAssignment(CtOperatorAssignment ctOperatorAssignment) {
        lhs = this.generate(ctOperatorAssignment.getAssigned());
        rhs = this.generate(ctOperatorAssignment.getAssignment());
        kind = ctOperatorAssignment.getKind();
    }

@Override
    public String toString() {
        return lhs.toString() + " " + CodeFactory.binaryOperator(kind) + "= " + rhs.toString();
    }
}
