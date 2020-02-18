package ca.frar.jjjrmi.jsbuilder.code;
import static ca.frar.jjjrmi.jsbuilder.code.JSConstructorCall.LOGGER;
import java.util.HashMap;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtFieldReference;

public class JSFieldDeclaration extends AbstractJSCodeElement{
    private final CtFieldReference<?> reference;
    private final JSCodeElement rhs;
    private boolean isStatic = false;

    public JSFieldDeclaration(CtField<?> ctField){
        LOGGER.trace(this.getClass().getSimpleName());
        if (ctField.hasModifier(ModifierKind.STATIC)) this.isStatic = true;
        reference = ctField.getReference();
        rhs = this.generate(ctField.getAssignment());
    }

    public boolean isStatic(){
        return isStatic;
    }

    @Override
    public String toString(){
        if (rhs == null || rhs.toString().isEmpty()){
            return "this." + reference.getSimpleName() + " = undefined;";
        } else {
            return "this." + reference.getSimpleName() + " = " + rhs.toString() + ";";
        }
    }

    public String staticString(String className){
        if (rhs == null || rhs.toString().isEmpty()){
            return className + "." + reference.getSimpleName() + " = undefined;";
        } else {
            return className + "." + reference.getSimpleName() + " = " + rhs.toString() + ";";
        }
    }
    
    public String toXML(int indent) {
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put("field", reference.toString());
        return toXML(indent, attributes);
    } 
}
