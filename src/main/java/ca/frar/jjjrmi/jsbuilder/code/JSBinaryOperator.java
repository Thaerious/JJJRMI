package ca.frar.jjjrmi.jsbuilder.code;

import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;

public class JSBinaryOperator implements JSCodeElement {

    private final BinaryOperatorKind kind;
    private final JSCodeElement lhs;
    private final JSCodeElement rhs;

    public JSBinaryOperator(CtBinaryOperator ctBinaryOperator) {
        kind = ctBinaryOperator.getKind();
        lhs = CodeFactory.generate(ctBinaryOperator.getLeftHandOperand());
        rhs = CodeFactory.generate(ctBinaryOperator.getRightHandOperand());
    }

    @Override
    public String toString() {
        return lhs.toString() + " " + CodeFactory.binaryOperator(kind) + " " + rhs.toString();
    }

}
