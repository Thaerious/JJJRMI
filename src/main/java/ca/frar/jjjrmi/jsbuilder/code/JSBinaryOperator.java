package ca.frar.jjjrmi.jsbuilder.code;

import ca.frar.jjjrmi.jsbuilder.JSCodeElement;
import ca.frar.jjjrmi.jsbuilder.AbstractJSCodeElement;
import static ca.frar.jjjrmi.Global.LOGGER;
import java.util.HashMap;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;

public class JSBinaryOperator extends AbstractJSCodeElement {
    private final BinaryOperatorKind kind;
    private final JSCodeElement lhs;
    private final JSCodeElement rhs;

    public JSBinaryOperator(CtBinaryOperator ctBinaryOperator) {
        LOGGER.trace(this.getClass().getSimpleName());
        kind = ctBinaryOperator.getKind();
        lhs = this.generate(ctBinaryOperator.getLeftHandOperand());
        rhs = this.generate(ctBinaryOperator.getRightHandOperand());
    }

    @Override
    public String toString() {
        return lhs.toString() + " " + CodeFactory.binaryOperator(kind) + " " + rhs.toString();
    }
    
    public String toXML(int indent) {
        HashMap<String, String> attributes = new HashMap<>();
        this.setAttr("op", kind.toString());
        return super.toXML(indent);
    }  

}
