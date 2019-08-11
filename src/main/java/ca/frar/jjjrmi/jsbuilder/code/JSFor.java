package ca.frar.jjjrmi.jsbuilder.code;
import spoon.reflect.code.CtFor;

public class JSFor implements JSCodeElement {
    private final JSElementList forInit;
    private final JSElementList forUpdate;
    private final JSCodeElement forExpression;
    private final JSCodeElement body;

    public JSFor(CtFor ctFor) {
        forInit = new JSElementList(ctFor.getForInit());
        forUpdate = new JSElementList(ctFor.getForUpdate());
        forExpression = CodeFactory.generate(ctFor.getExpression());
        body = CodeFactory.generate(ctFor.getBody());
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
