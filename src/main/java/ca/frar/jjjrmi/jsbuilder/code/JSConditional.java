package ca.frar.jjjrmi.jsbuilder.code;

import ca.frar.jjjrmi.jsbuilder.JSCodeElement;
import ca.frar.jjjrmi.jsbuilder.AbstractJSCodeElement;
import static ca.frar.jjjrmi.Global.LOGGER;
import spoon.reflect.code.CtConditional;

public class JSConditional extends AbstractJSCodeElement {

    private final JSCodeElement condition;
    private final JSCodeElement elseEx;
    private final JSCodeElement thenEx;

    public JSConditional(CtConditional ctConditional) {
        LOGGER.trace(this.getClass().getSimpleName());
        condition = this.generate(ctConditional.getCondition());
        elseEx = this.generate(ctConditional.getElseExpression());
        thenEx = this.generate(ctConditional.getThenExpression());
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
