package ca.frar.jjjrmi.jsbuilder.code;

import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;

public class JSBinaryOperator extends AbstractJSCodeElement {

    private final BinaryOperatorKind kind;
    private final JSCodeElement lhs;
    private final JSCodeElement rhs;

    public JSBinaryOperator(CtBinaryOperator ctBinaryOperator) {
        kind = ctBinaryOperator.getKind();
        lhs = this.generate(ctBinaryOperator.getLeftHandOperand());
        rhs = this.generate(ctBinaryOperator.getRightHandOperand());
    }

    @Override
    public String toString() {
        return lhs.toString() + " " + CodeFactory.binaryOperator(kind) + " " + rhs.toString();
    }

}
