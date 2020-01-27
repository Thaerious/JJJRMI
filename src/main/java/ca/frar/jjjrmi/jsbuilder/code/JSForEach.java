package ca.frar.jjjrmi.jsbuilder.code;
import spoon.reflect.code.CtForEach;

public class JSForEach extends AbstractJSCodeElement {

    private final JSCodeElement body;
    private final JSCodeElement expression;
    private final JSCodeElement variable;

    public JSForEach(CtForEach ctForEach) {
        body = this.generate(ctForEach.getBody());
        expression = this.generate(ctForEach.getExpression());
        variable = this.generate(ctForEach.getVariable());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("for(");
        builder.append(variable);
        builder.append(" of ");
        builder.append(expression);
        builder.append(")");
        builder.append(body.scoped());

        return builder.toString();
    }

}
