package ca.frar.jjjrmi.jsbuilder.code;
import ca.frar.jjjrmi.jsbuilder.JSCodeElement;
import ca.frar.jjjrmi.jsbuilder.AbstractJSCodeElement;
import static ca.frar.jjjrmi.jsbuilder.code.JSConstructorCall.LOGGER;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.reference.CtFieldReference;

public class JSFieldRead extends AbstractJSCodeElement {
    private final CtFieldReference variable;
    private final JSCodeElement target;

    public JSFieldRead(CtFieldRead ctFieldRead) {
        LOGGER.trace(this.getClass().getSimpleName());
        variable = ctFieldRead.getVariable();
        target = this.generate(ctFieldRead.getTarget());
    }

    @Override
    public String toString(){
        return target.toString() + "." + variable.getSimpleName();
    }
}
