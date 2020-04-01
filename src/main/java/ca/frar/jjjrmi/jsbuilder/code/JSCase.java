package ca.frar.jjjrmi.jsbuilder.code;

import ca.frar.jjjrmi.jsbuilder.JSCodeElement;
import ca.frar.jjjrmi.jsbuilder.AbstractJSCodeElement;
import ca.frar.jjjrmi.jsbuilder.JSElementList;
import static ca.frar.jjjrmi.Global.LOGGER;
import spoon.reflect.code.CtCase;

public class JSCase extends AbstractJSCodeElement {

    private final JSCodeElement expression;
    private final JSElementList body;

    public JSCase(CtCase<?> ctCase) {
        LOGGER.trace(this.getClass().getSimpleName());
        expression = this.generate(ctCase.getCaseExpression());
        body = this.generateList(ctCase.getStatements());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (expression.getClass() == JSEmptyElement.class) {
            builder.append("default: ");
        } else {
            builder.append("case ");
            builder.append(expression);
            builder.append(": ");
        }
        builder.append(body);
        return builder.toString();
    }
}
