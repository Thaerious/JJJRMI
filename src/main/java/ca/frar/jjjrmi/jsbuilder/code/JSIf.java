package ca.frar.jjjrmi.jsbuilder.code;
import spoon.reflect.code.CtIf;

public class JSIf extends AbstractJSCodeElement {

    private final JSCodeElement condition;
    private final JSCodeElement thenStatement;
    private final JSCodeElement elseStatement;

    public JSIf(CtIf ctIf) {
        condition = this.generate(ctIf.getCondition());
        thenStatement = this.generate(ctIf.getThenStatement());
        elseStatement = this.generate(ctIf.getElseStatement());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("if (");
        builder.append(condition);
        builder.append(")");

        if (!thenStatement.isEmpty()) {
            builder.append(thenStatement.toString());
        }

        if (!elseStatement.isEmpty()) {
            builder.append("else ");
            builder.append(elseStatement);
        }

        return builder.toString();
    }
}
