package ca.frar.jjjrmi.jsbuilder.code;
import ca.frar.jjjrmi.jsbuilder.AbstractJSCodeElement;
import static ca.frar.jjjrmi.Global.LOGGER;
import java.util.HashMap;
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
    
    public String toXML(int indent) {
        HashMap<String, String> attributes = new HashMap<>();
        this.setAttr("name", variable.getSimpleName());
        return super.toXML(indent);
    }    

}
