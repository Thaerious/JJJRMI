package ca.frar.jjjrmi.jsbuilder.code;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.reference.CtVariableReference;

public class JSVariableRead implements JSCodeElement {

    private final CtVariableReference variable;

    public JSVariableRead(CtVariableRead ctVariableRead) {
        variable = ctVariableRead.getVariable();
    }

    @Override
    public String toString(){
        return variable.getSimpleName();
    }

}
