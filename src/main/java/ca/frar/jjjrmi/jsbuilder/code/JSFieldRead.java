package ca.frar.jjjrmi.jsbuilder.code;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.reference.CtFieldReference;

public class JSFieldRead extends AbstractJSCodeElement {
    private final CtFieldReference variable;
    private final JSCodeElement target;

    public JSFieldRead(CtFieldRead ctFieldRead) {
        variable = ctFieldRead.getVariable();
        target = this.generate(ctFieldRead.getTarget());
    }

    @Override
    public String toString(){
        return target.toString() + "." + variable.getSimpleName();
    }
}
