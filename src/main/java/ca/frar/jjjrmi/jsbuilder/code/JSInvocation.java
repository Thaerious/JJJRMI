package ca.frar.jjjrmi.jsbuilder.code;
import ca.frar.jjjrmi.annotations.DoNotInvoke;
import java.util.Set;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

public class JSInvocation extends AbstractJSCodeElement {
    private final JSElementList arguments;
    private final String name;
    private final JSCodeElement target;
    private String specialCase = null;
    private final CtInvocation<?> invocation;

    public JSInvocation(CtInvocation<?> ctInvocation) {   
        this.invocation = ctInvocation;
        arguments = new JSElementList(ctInvocation.getArguments());
        name = ctInvocation.getExecutable().getSimpleName();
        target = CodeFactory.generate(ctInvocation.getTarget());
        checkForSpecialCase(ctInvocation);
    }

    /**
     * Return true if this invocation should be processed.  Return false to
     * not process this invocation.
     * @param ctInvocation
     * @return 
     */
    private boolean checkInvocation(){
        CtTypeReference<?> type = invocation.getTarget().getType();
        CtType<?> typeDeclaration = type.getTypeDeclaration();
        Set<CtMethod<?>> methods = typeDeclaration.getAllMethods();
        
        for (CtMethod<?> method : methods){
            if (method.getSimpleName().equals(this.name) && method.hasAnnotation(DoNotInvoke.class)){
                return false;
            }
        }
        
        return true;        
    }
    
    public String toString(){
        if (specialCase != null) return specialCase;
        if (checkInvocation()){
            return target.toString() + "." + name + "(" + arguments.inline() + ")";
        } else {
            return "/* method invocation revoked */";
        }
    }

    private void checkForSpecialCase(CtInvocation<?> ctInvocation){
        CtExpression<?> ctTarget = ctInvocation.getTarget();
        CtTypeReference<?> ctType = ctTarget.getType();
        if (ctType.toString().equals("java.lang.String")) processSpecialCaseString(ctInvocation);
    }

    private void processSpecialCaseString(CtInvocation<?> ctInvocation){
        switch (ctInvocation.getExecutable().getSimpleName()){
            case "equals":{
                StringBuilder builder = new StringBuilder();
                builder.append(target.toString());
                builder.append(" === ");
                builder.append(arguments.inline());
                specialCase = builder.toString();
            }break;
        }
    }
}
