package ca.frar.jjjrmi.jsbuilder.code;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.UnaryOperatorKind;

public class JSUnaryOperator implements JSCodeElement {

    private final JSCodeElement operand;
    private final UnaryOperatorKind kind;

    public JSUnaryOperator(CtUnaryOperator<?> ctUnaryOperator) {
        this.operand = CodeFactory.generate(ctUnaryOperator.getOperand());
        kind = ctUnaryOperator.getKind();
    }

    @Override
    public String toString() {

        switch (kind) {
            case POS:
                return "+" + operand.toString();
            case NEG:
                return "-" + operand.toString();
            case NOT:
                return "!" + operand.toString();
            case COMPL:
                return "~" + operand.toString();
            case PREINC:
                return "++" + operand.toString();
            case PREDEC:
                return "--" + operand.toString();
            case POSTINC:
                return operand.toString() + "++";
            case POSTDEC:
                return operand.toString() + "--";
            default:
                return operand.toString();
        }

    }

}
