package ca.frar.jjjrmi.jsbuilder.code;
import ca.frar.jjjrmi.jsbuilder.JSCodeElement;
import ca.frar.jjjrmi.jsbuilder.AbstractJSCodeElement;
import static ca.frar.jjjrmi.jsbuilder.code.JSConstructorCall.LOGGER;
import spoon.reflect.code.CtLocalVariable;

public class JSLocalVariable extends AbstractJSCodeElement{
    private final CtLocalVariable ctLocalVariable;
    private final JSLocalVariableReference lhs;
    private final JSCodeElement rhs;

    public JSLocalVariable(CtLocalVariable ctLocalVariable){
        LOGGER.trace(this.getClass().getSimpleName());
        this.ctLocalVariable = ctLocalVariable;
        this.lhs = new JSLocalVariableReference(ctLocalVariable.getReference());
        this.rhs = this.generate(ctLocalVariable.getAssignment());

        this.setAttr("name", this.lhs);
    }

    @Override
    public String toString(){
        if (this.noChildren()) return "let " + lhs.toString();
        else return "let " + lhs.toString() + " = " + rhs.toString();
    }
}
