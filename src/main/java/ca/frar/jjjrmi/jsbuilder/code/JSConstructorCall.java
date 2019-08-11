package ca.frar.jjjrmi.jsbuilder.code;
import ca.frar.jjjrmi.utility.JJJOptionsHandler;
import spoon.reflect.code.CtConstructorCall;

public class JSConstructorCall implements JSCodeElement {
    private final JSElementList arguments;
    private final String name;
    private final CtConstructorCall ctConstructorCall;

    public JSConstructorCall(CtConstructorCall ctConstructorCall) {
        this.ctConstructorCall = ctConstructorCall;
        arguments = new JSElementList(ctConstructorCall.getArguments());
        name = ctConstructorCall.getType().getSimpleName();
    }

    @Override
    public String toString() {
        JJJOptionsHandler jjjOptionsHandler = new JJJOptionsHandler(ctConstructorCall.getType().getTypeDeclaration());
                
        StringBuilder builder = new StringBuilder();
        builder.append("new ");
        if (jjjOptionsHandler.hasJJJ() && !jjjOptionsHandler.getClass().isEnum()){
            builder.append(jjjOptionsHandler.getName());
        } else {        
            builder.append(name);
        }
        builder.append("(");
        builder.append(arguments.inline());
        builder.append(")");
        return builder.toString();
    }

}
