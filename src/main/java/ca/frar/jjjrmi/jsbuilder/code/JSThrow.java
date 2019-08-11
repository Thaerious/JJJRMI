package ca.frar.jjjrmi.jsbuilder.code;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtThrow;

public class JSThrow implements JSCodeElement {

    private final CtExpression<? extends Throwable> thrownExpression;

    public JSThrow(CtThrow ctThrow) {
        thrownExpression = ctThrow.getThrownExpression();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("throw new Error(\"");
        builder.append(thrownExpression.getType());
        builder.append("\")");
        return builder.toString();
    }
}
