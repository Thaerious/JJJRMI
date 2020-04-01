package ca.frar.jjjrmi.jsbuilder.code;
import ca.frar.jjjrmi.jsbuilder.JSCodeElement;
import ca.frar.jjjrmi.jsbuilder.AbstractJSCodeElement;
import static ca.frar.jjjrmi.jsbuilder.code.JSConstructorCall.LOGGER;
import spoon.reflect.code.CtIf;

public class JSIf extends AbstractJSCodeElement {

    private final JSCodeElement condition;
    private final JSCodeElement thenStatement;
    private final JSCodeElement elseStatement;

    public JSIf(CtIf ctIf) {
        LOGGER.trace(this.getClass().getSimpleName());
        condition = this.generate(ctIf.getCondition());        
        thenStatement = this.generate(ctIf.getThenStatement());
        elseStatement = this.generate(ctIf.getElseStatement());
        thenStatement.setAttr("semantic", "then");
        elseStatement.setAttr("semantic", "else");
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("if (");
        builder.append(condition);
        builder.append(")");

        builder.append(thenStatement.toString());

        if (elseStatement.hasChildren()) {
            builder.append("else ");
            builder.append(elseStatement);
        }

        return builder.toString();
    }
}
