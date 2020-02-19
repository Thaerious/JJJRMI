package ca.frar.jjjrmi.jsbuilder.code;
import static ca.frar.jjjrmi.Global.LOGGER;
import spoon.reflect.code.CtInvocation;

public class JSSuperConstructor extends AbstractJSCodeElement {
    private final JSElementList arguments;

    public JSSuperConstructor(CtInvocation<?> ctInvocation) {
        LOGGER.trace(this.getClass().getSimpleName());
        this.arguments = this.generateList(ctInvocation.getArguments());
    }

    public JSSuperConstructor() {
        arguments = new JSElementList();
    }    
    
    @Override
    public String toString(){
        return "super(" + arguments.inline() + ")";
    }
}
