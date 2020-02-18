package ca.frar.jjjrmi.jsbuilder.code;
import static ca.frar.jjjrmi.jsbuilder.code.JSConstructorCall.LOGGER;
import java.util.HashMap;
import spoon.reflect.code.CtLiteral;

public class JSLiteral extends AbstractJSCodeElement {
    private final CtLiteral ctLiteral;

    public JSLiteral(CtLiteral<?> ctLiteral) {
        LOGGER.trace(this.getClass().getSimpleName());
        this.ctLiteral = ctLiteral;
    }

    @Override
    public String toString(){
        Object value = ctLiteral.getValue();
        if (value == null) return "null";
        else if (value instanceof Number) return value.toString();
        else return ctLiteral.toString();
    }
    
    public String toXML(int indent) {
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put("value", ctLiteral.toString());
        attributes.put("type", ctLiteral.getType().toString());
        return toXML(indent, attributes);
    }      
}