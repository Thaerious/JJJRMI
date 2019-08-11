package ca.frar.jjjrmi.jsbuilder.code;
import spoon.reflect.code.CtLocalVariable;

public class JSLocalVariable implements JSCodeElement{
    private final CtLocalVariable ctLocalVariable;
    private final JSLocalVariableReference lhs;
    private final JSCodeElement rhs;

    public JSLocalVariable(CtLocalVariable ctLocalVariable){
        this.ctLocalVariable = ctLocalVariable;
        this.lhs = new JSLocalVariableReference(ctLocalVariable.getReference());
        this.rhs = CodeFactory.generate(ctLocalVariable.getAssignment());
    }

    @Override
    public String toString(){
        if (rhs.isEmpty()) return "let " + lhs.toString();
        else return "let " + lhs.toString() + " = " + rhs.toString();
    }

}
