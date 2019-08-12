package ca.frar.jjjrmi.jsbuilder.code;

import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.reference.CtVariableReference;

public class JSVariableWrite extends AbstractJSCodeElement {
    private final CtVariableReference variable;

    public JSVariableWrite(CtVariableWrite ctVariableWrite) {
        variable = ctVariableWrite.getVariable();
    }

    @Override
    public String toString(){
        return variable.toString();
    }

}
