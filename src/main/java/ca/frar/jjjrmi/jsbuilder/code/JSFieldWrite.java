package ca.frar.jjjrmi.jsbuilder.code;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.reference.CtFieldReference;

public class JSFieldWrite extends AbstractJSCodeElement {

    private final JSCodeElement target;
    private final CtFieldReference variable;

    public JSFieldWrite(CtFieldWrite ctFieldWrite) {
        variable = ctFieldWrite.getVariable();
        target = CodeFactory.generate(ctFieldWrite.getTarget());
    }

    @Override
    public String toString(){
        return target + "." + variable.getSimpleName();
    }
}
