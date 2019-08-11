package ca.frar.jjjrmi.jsbuilder.code;
import spoon.reflect.code.CtIf;

public class JSIf implements JSCodeElement {

    private final JSCodeElement condition;
    private final JSCodeElement thenStatement;
    private final JSCodeElement elseStatement;

    public JSIf(CtIf ctIf) {
        condition = CodeFactory.generate(ctIf.getCondition());
        thenStatement = CodeFactory.generate(ctIf.getThenStatement());
        elseStatement = CodeFactory.generate(ctIf.getElseStatement());
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
