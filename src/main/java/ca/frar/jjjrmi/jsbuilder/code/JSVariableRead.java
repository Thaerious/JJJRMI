package ca.frar.jjjrmi.jsbuilder.code;
import static ca.frar.jjjrmi.Global.LOGGER;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.reference.CtVariableReference;

public class JSVariableRead extends AbstractJSCodeElement {

    private final CtVariableReference variable;

    public JSVariableRead(CtVariableRead ctVariableRead) {
        LOGGER.trace(this.getClass().getSimpleName());
        variable = ctVariableRead.getVariable();
    }

    @Override
    public String toString(){
        return variable.getSimpleName();
    }

}
