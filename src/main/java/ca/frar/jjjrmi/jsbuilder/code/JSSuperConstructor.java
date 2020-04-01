package ca.frar.jjjrmi.jsbuilder.code;
import ca.frar.jjjrmi.jsbuilder.AbstractJSCodeElement;
import ca.frar.jjjrmi.jsbuilder.JSElementList;
import static ca.frar.jjjrmi.Global.LOGGER;
import spoon.reflect.code.CtInvocation;

public class JSSuperConstructor extends AbstractJSCodeElement {
    private final JSElementList arguments;

    public JSSuperConstructor(CtInvocation<?> ctInvocation) {
        LOGGER.trace(this.getClass().getSimpleName());
        this.arguments = this.generateList(ctInvocation.getArguments());
    }

    public JSSuperConstructor() {
        arguments = this.generateList();
    }    
    
    @Override
    public String toString(){
        return "super(" + arguments.inline() + ")";
    }
}
