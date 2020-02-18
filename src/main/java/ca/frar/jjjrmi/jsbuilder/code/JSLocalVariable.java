package ca.frar.jjjrmi.jsbuilder.code;
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
    }

    @Override
    public String toString(){
        if (rhs.isEmpty()) return "let " + lhs.toString();
        else return "let " + lhs.toString() + " = " + rhs.toString();
    }
}
