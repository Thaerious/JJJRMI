package ca.frar.jjjrmi.jsbuilder.code;

import spoon.reflect.reference.CtLocalVariableReference;

public class JSLocalVariableReference implements JSCodeElement{
    private final CtLocalVariableReference ctLocalVariableReference;

    public JSLocalVariableReference(CtLocalVariableReference ctLocalVariableReference){
        this.ctLocalVariableReference = ctLocalVariableReference;
    }

    @Override
    public String toString(){
        return ctLocalVariableReference.toString();
    }

}
