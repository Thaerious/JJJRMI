package ca.frar.jjjrmi.jsbuilder.code;

import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtOperatorAssignment;

public class JSOperatorAssignment implements JSCodeElement {

    private final BinaryOperatorKind kind;
    private final JSCodeElement rhs;
    private final JSCodeElement lhs;

    public JSOperatorAssignment(CtOperatorAssignment ctOperatorAssignment) {
        lhs = CodeFactory.generate(ctOperatorAssignment.getAssigned());
        rhs = CodeFactory.generate(ctOperatorAssignment.getAssignment());
        kind = ctOperatorAssignment.getKind();
    }

@Override
    public String toString() {
        return lhs.toString() + " " + CodeFactory.binaryOperator(kind) + "= " + rhs.toString();
    }
}
