package ca.frar.jjjrmi.jsbuilder.code;

import spoon.reflect.code.CtCase;

public class JSCase implements JSCodeElement {

    private final JSCodeElement expression;
    private final JSElementList body;

    public JSCase(CtCase<?> ctCase) {
        expression = CodeFactory.generate(ctCase.getCaseExpression());
        body = new JSElementList(ctCase.getStatements());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (expression.isEmpty()) {
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
