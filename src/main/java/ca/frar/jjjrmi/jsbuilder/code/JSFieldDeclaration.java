package ca.frar.jjjrmi.jsbuilder.code;
import ca.frar.jjjrmi.jsbuilder.JSCodeElement;
import ca.frar.jjjrmi.jsbuilder.AbstractJSCodeElement;
import static ca.frar.jjjrmi.jsbuilder.code.JSConstructorCall.LOGGER;
import java.util.HashMap;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtFieldReference;

public class JSFieldDeclaration extends AbstractJSCodeElement{
    private final CtFieldReference<?> reference;
    private final JSCodeElement rhs;
    private boolean isStatic = false;
    private final String invoker;
    private final CtType<?> declaringType;

    public JSFieldDeclaration(CtField<?> ctField){
        LOGGER.trace(this.getClass().getSimpleName());
        if (ctField.hasModifier(ModifierKind.STATIC)) this.isStatic = true;
        reference = ctField.getReference();
        rhs = this.generate(ctField.getAssignment());
        
        declaringType = ctField.getDeclaringType();
        
        if (this.isStatic){
            this.invoker = ctField.getDeclaringType().getSimpleName();
        } else {
            this.invoker = "this";
        }
    }

    public boolean isStatic(){
        return isStatic;
    }

    @Override
    public String toString(){
        if (rhs == null || rhs.toString().isEmpty()){
            return this.invoker + "." + reference.getSimpleName() + " = undefined;";
        } else {
            return this.invoker + "." + reference.getSimpleName() + " = " + rhs.toString() + ";";
        }
    }
    
    public String toXML(int indent) {
        HashMap<String, String> attributes = new HashMap<>();
        this.setAttr("field", reference.toString());
        this.setAttr("static", "" + this.isStatic);
        this.setAttr("invoker", "" + this.invoker);
        this.setAttr("declaring-type", "" + this.declaringType.getQualifiedName());
        return super.toXML(indent);
    } 
}
