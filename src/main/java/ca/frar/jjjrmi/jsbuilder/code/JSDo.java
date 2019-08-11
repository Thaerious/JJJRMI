package ca.frar.jjjrmi.jsbuilder.code;

import spoon.reflect.code.CtDo;

public class JSDo implements JSCodeElement {
    private final JSCodeElement expression;
    private final JSBlock body;

    public JSDo(CtDo ctDo) {
        expression = CodeFactory.generate(ctDo.getLoopingExpression());
        body = (JSBlock) CodeFactory.generate(ctDo.getBody());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("do");
        builder.append(body.scoped());
        builder.append("while(");
        builder.append(expression);
        builder.append(")");
        return builder.toString();
    }
}
