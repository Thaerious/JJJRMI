package ca.frar.jjjrmi.jsbuilder.code;
import static ca.frar.jjjrmi.jsbuilder.code.JSConstructorCall.LOGGER;
import java.util.HashMap;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.reference.CtFieldReference;

public class JSFieldWrite extends AbstractJSCodeElement {

    private final JSCodeElement target;
    private final CtFieldReference variable;

    public JSFieldWrite(CtFieldWrite ctFieldWrite) {
        LOGGER.trace(this.getClass().getSimpleName());
        variable = ctFieldWrite.getVariable();
        target = this.generate(ctFieldWrite.getTarget());
    }

    @Override
    public String toString(){
        return target + "." + variable.getSimpleName();
    }
    
    public String toXML(int indent) {
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put("field", variable.toString());
        return toXML(indent, attributes);
    }    
}
