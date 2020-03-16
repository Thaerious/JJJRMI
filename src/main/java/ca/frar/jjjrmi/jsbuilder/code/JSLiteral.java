package ca.frar.jjjrmi.jsbuilder.code;

import static ca.frar.jjjrmi.jsbuilder.code.JSConstructorCall.LOGGER;
import java.util.HashMap;
import java.util.Scanner;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.reference.CtTypeReference;

public class JSLiteral extends AbstractJSCodeElement {

    private final CtLiteral ctLiteral;
    private final String value;
    
    public JSLiteral(CtLiteral ctLiteral) {
        LOGGER.trace(this.getClass().getSimpleName());
        this.ctLiteral = ctLiteral;
        
        if (ctLiteral.getValue() == null){
            value = "null";
        }
        else if (ctLiteral.getValue().getClass() == Character.class){
            this.value = "'" + ctLiteral.getValue() + "'";
        }
        else if (ctLiteral.getValue().getClass() == String.class){
            this.value = "\"" + ctLiteral.getValue() + "\"";
        }
        else {
            this.value = "" + ctLiteral.getValue();
        }
    }

    @Override
    public String toString() {
        return value;
    }

    public String toXML(int indent) {
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put("value", value);
        attributes.put("type", ctLiteral.getType().toString());
        return toXML(indent, attributes);
    }
}
