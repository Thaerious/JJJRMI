package ca.frar.jjjrmi.jsbuilder.code;
import spoon.reflect.code.CtInvocation;

public class JSSuperConstructor extends AbstractJSCodeElement {
    private final JSElementList arguments;

    public JSSuperConstructor(CtInvocation <?> ctInvocation) {
        arguments = new JSElementList(ctInvocation.getArguments());
    }

    public JSSuperConstructor() {
        arguments = new JSElementList();
    }    
    
    @Override
    public String toString(){
        return "super(" + arguments.inline() + ")";
    }
}
