package ca.frar.jjjrmi.jsbuilder.code;
import ca.frar.jjjrmi.exceptions.TypeDeclarationNotFoundWarning;
import ca.frar.jjjrmi.utility.JJJOptionsHandler;
import java.util.HashSet;
import java.util.Set;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.reference.CtTypeReference;

public class JSConstructorCall extends AbstractJSCodeElement {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JJJRMI");
    private final JSElementList arguments;
    private final String name;
    private final CtConstructorCall ctConstructorCall;

    public JSConstructorCall(CtConstructorCall ctConstructorCall) {
        LOGGER.trace(this.getClass().getSimpleName());
        this.ctConstructorCall = ctConstructorCall;
        arguments = new JSElementList(ctConstructorCall.getArguments());
        name = ctConstructorCall.getType().getSimpleName();
    }
    
    @Override
    public Set<CtTypeReference> getRequires() {
        HashSet<CtTypeReference> requires = new HashSet<>();
        requires.addAll(super.getRequires());
        requires.add(ctConstructorCall.getType());        
        return requires;
    }
    

    @Override
    public String toString() {
        JJJOptionsHandler jjjOptionsHandler = new JJJOptionsHandler(ctConstructorCall.getType().getTypeDeclaration());
                
        StringBuilder builder = new StringBuilder();
        builder.append("new ");
        if (!jjjOptionsHandler.getClass().isEnum()){
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
