package ca.frar.jjjrmi.jsbuilder.code;
import ca.frar.jjjrmi.annotations.DoNotInvoke;
import static ca.frar.jjjrmi.jsbuilder.code.JSConstructorCall.LOGGER;
import ca.frar.jjjrmi.socket.JJJObject;
import ca.frar.jjjrmi.utility.JJJOptionsHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

public class JSInvocation extends AbstractJSCodeElement {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(JSInvocation.class);
    private final JSElementList arguments;
    private String name;
    private final JSCodeElement target;
    private String specialCase = null;
    private final CtInvocation<?> invocation;

    public JSInvocation(CtInvocation<?> ctInvocation) {
        LOGGER.trace(this.getClass().getSimpleName());
        this.invocation = ctInvocation;
        arguments = this.generateList(ctInvocation.getArguments());
        name = ctInvocation.getExecutable().getSimpleName();
        target = this.generate(ctInvocation.getTarget());
        checkForSpecialCase(ctInvocation);
    }

    public String toXML(int indent) {
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put("name", name);
        return toXML(indent, attributes);
    }       
    
    /**
     * Return true if this invocation should be processed. Return false to not
     * process this invocation.
     *
     * @param ctInvocation
     * @return
     */
    private boolean checkInvocation() {
        if (invocation.getTarget() == null) return true; // super(...)
        CtTypeReference<?> type = invocation.getTarget().getType();
        CtType<?> typeDeclaration = type.getTypeDeclaration();
        Set<CtMethod<?>> methods = typeDeclaration.getAllMethods();

        for (CtMethod<?> method : methods) {
            if (method.getSimpleName().equals(this.name) && method.hasAnnotation(DoNotInvoke.class)) {
                return false;
            }
        }

        return true;
    }

    public String toString() {
        if (specialCase != null) return specialCase;
                
        if (name.equals("<init>")){
            return "";
        }
        else if (checkInvocation()) {
            return target.toString() + "." + name + "(" + arguments.inline() + ")";
        } else {
            return "/* method invocation revoked */";
        }
    }

    private boolean checkForSuperclass(){
        CtType<?> type = this.invocation.getType().getDeclaration();        
        
        if (type == null){
            return false;
        }
        
        JJJOptionsHandler jjjOptions = new JJJOptionsHandler(type);
        
        if (jjjOptions.hasExtends()) {
            return true;
        }
        
        if (type.getSimpleName().equals(JJJObject.class.getSimpleName())) {
            return false;
        }
        else {
            return true;
        }
    }
    
    private void checkForSpecialCase(CtInvocation<?> ctInvocation) {
        CtExpression<?> ctTarget = ctInvocation.getTarget();
        if (ctTarget != null) {
            CtTypeReference<?> ctType = ctTarget.getType();
            if (ctType.toString().equals("java.lang.String")) processSpecialCaseString(ctInvocation);
        }
    }

    private void processSpecialCaseString(CtInvocation<?> ctInvocation) {
        switch (ctInvocation.getExecutable().getSimpleName()) {
            case "equals": {
                StringBuilder builder = new StringBuilder();
                builder.append(target.toString());
                builder.append(" === ");
                builder.append(arguments.inline());
                specialCase = builder.toString();
            }
            break;
        }
    }
}
