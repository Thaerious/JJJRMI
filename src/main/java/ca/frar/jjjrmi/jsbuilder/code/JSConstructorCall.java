package ca.frar.jjjrmi.jsbuilder.code;
import static ca.frar.jjjrmi.Global.LOGGER;
import ca.frar.jjjrmi.exceptions.TypeDeclarationNotFoundWarning;
import ca.frar.jjjrmi.utility.JJJOptionsHandler;
import java.util.HashSet;
import java.util.Set;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.declaration.CtType;
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
        LOGGER.debug("JSConstructorCall.getRequires");
        HashSet<CtTypeReference> requires = new HashSet<>();
        requires.addAll(super.getRequires());
        requires.add(ctConstructorCall.getType()); 
        LOGGER.debug("requires.add " + ctConstructorCall.getType());
        return requires;
    }
    

    @Override
    public String toString() {
        CtTypeReference constType = ctConstructorCall.getType();
        CtType constTypeDec = constType.getTypeDeclaration();
                
        StringBuilder builder = new StringBuilder();
        builder.append("new ");
        if (constType.isEnum()){
            if (constTypeDec != null){
                JJJOptionsHandler jjjOpt = new JJJOptionsHandler(constTypeDec);
                builder.append(jjjOpt.getName());
            } else {
                builder.append(constType.getSimpleName());
            }
        } else {        
            builder.append(name);
        }
        builder.append("(");
        builder.append(arguments.inline());
        builder.append(")");
        return builder.toString();
    }
}
