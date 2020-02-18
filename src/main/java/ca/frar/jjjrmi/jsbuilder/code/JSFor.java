package ca.frar.jjjrmi.jsbuilder.code;
import static ca.frar.jjjrmi.jsbuilder.code.JSConstructorCall.LOGGER;
import spoon.reflect.code.CtFor;

public class JSFor extends AbstractJSCodeElement {
    private final JSElementList forInit;
    private final JSElementList forUpdate;
    private final JSCodeElement forExpression;
    private final JSCodeElement body;

    public JSFor(CtFor ctFor) {
        LOGGER.trace(this.getClass().getSimpleName());
        forInit = new JSElementList(ctFor.getForInit());
        forUpdate = new JSElementList(ctFor.getForUpdate());
        forExpression = this.generate(ctFor.getExpression());
        body = this.generate(ctFor.getBody());
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("for(");
        builder.append(forInit.inline());
        builder.append("; ");
        builder.append(forExpression.toString());
        builder.append("; ");
        builder.append(forUpdate.inline());
        builder.append(")");
        builder.append(body.toString());
        return builder.toString();
    }
}
