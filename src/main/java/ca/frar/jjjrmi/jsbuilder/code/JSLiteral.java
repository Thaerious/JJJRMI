package ca.frar.jjjrmi.jsbuilder.code;
import spoon.reflect.code.CtLiteral;

public class JSLiteral extends AbstractJSCodeElement {
    private final CtLiteral ctLiteral;

    public JSLiteral(CtLiteral<?> ctLiteral) {
        this.ctLiteral = ctLiteral;
    }

    @Override
    public String toString(){
        Object value = ctLiteral.getValue();
        if (value == null) return "null";
        else if (value instanceof Number) return value.toString();
        else return ctLiteral.toString();
    }
}