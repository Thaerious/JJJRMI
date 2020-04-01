package ca.frar.jjjrmi.jsbuilder.code;
import ca.frar.jjjrmi.jsbuilder.JSCodeElement;
import ca.frar.jjjrmi.jsbuilder.AbstractJSCodeElement;
import static ca.frar.jjjrmi.Global.LOGGER;
import spoon.reflect.code.CtArrayRead;

public class JSArrayRead extends AbstractJSCodeElement {
    private final JSCodeElement target;
    private final JSCodeElement index;

    public JSArrayRead(CtArrayRead<?> ctArrayRead) {
        LOGGER.trace(this.getClass().getSimpleName());
        target = this.generate(ctArrayRead.getTarget());
        index = this.generate(ctArrayRead.getIndexExpression());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(target);
        builder.append("[");
        builder.append(index);
        builder.append("]");
        return builder.toString();
    }
}