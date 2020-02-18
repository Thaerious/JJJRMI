package ca.frar.jjjrmi.jsbuilder.code;
import static ca.frar.jjjrmi.Global.LOGGER;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtThrow;

public class JSThrow extends AbstractJSCodeElement {

    private final CtExpression<? extends Throwable> thrownExpression;

    public JSThrow(CtThrow ctThrow) {
        LOGGER.trace(this.getClass().getSimpleName());
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
