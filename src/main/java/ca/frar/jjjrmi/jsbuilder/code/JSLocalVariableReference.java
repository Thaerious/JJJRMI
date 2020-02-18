package ca.frar.jjjrmi.jsbuilder.code;

import static ca.frar.jjjrmi.jsbuilder.code.JSConstructorCall.LOGGER;
import spoon.reflect.reference.CtLocalVariableReference;

public class JSLocalVariableReference extends AbstractJSCodeElement{
    private final CtLocalVariableReference ctLocalVariableReference;

    public JSLocalVariableReference(CtLocalVariableReference ctLocalVariableReference){
        LOGGER.trace(this.getClass().getSimpleName());
        this.ctLocalVariableReference = ctLocalVariableReference;
    }

    @Override
    public String toString(){
        return ctLocalVariableReference.toString();
    }

}
