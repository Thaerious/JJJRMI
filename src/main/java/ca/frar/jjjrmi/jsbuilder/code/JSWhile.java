package ca.frar.jjjrmi.jsbuilder.code;
import spoon.reflect.code.CtWhile;

public class JSWhile extends AbstractJSCodeElement {
    private final JSCodeElement expression;
    private final JSCodeElement body;

    public JSWhile(CtWhile ctWhile) {
        expression = this.generate(ctWhile.getLoopingExpression());
        body = this.generate(ctWhile.getBody());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("while(");
        if (expression != null) builder.append(expression);
        builder.append(")");
        builder.append(body);
        return builder.toString();
    }
}