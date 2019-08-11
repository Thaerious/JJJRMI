package ca.frar.jjjrmi.jsbuilder.code;

import spoon.reflect.code.CtConditional;

public class JSConditional implements JSCodeElement {

    private final JSCodeElement condition;
    private final JSCodeElement elseEx;
    private final JSCodeElement thenEx;

    public JSConditional(CtConditional ctConditional) {
        condition = CodeFactory.generate(ctConditional.getCondition());
        elseEx = CodeFactory.generate(ctConditional.getElseExpression());
        thenEx = CodeFactory.generate(ctConditional.getThenExpression());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(condition);
        builder.append(" ? ");
        builder.append(thenEx);
        builder.append(" : ");
        builder.append(elseEx);
        return builder.toString();
    }

}
