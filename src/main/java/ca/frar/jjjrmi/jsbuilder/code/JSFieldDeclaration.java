package ca.frar.jjjrmi.jsbuilder.code;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtFieldReference;

public class JSFieldDeclaration extends AbstractJSCodeElement{
    private final CtFieldReference<?> reference;
    private final JSCodeElement rhs;
    private boolean isStatic = false;

    public JSFieldDeclaration(CtField<?> ctField){
        if (ctField.hasModifier(ModifierKind.STATIC)) this.isStatic = true;
        reference = ctField.getReference();
        rhs = this.generate(ctField.getAssignment());
    }

    public boolean isStatic(){
        return isStatic;
    }

    public boolean hasAssignment(){
        return rhs != null && !rhs.isEmpty();
    }

    @Override
    public String toString(){
        return "this." + reference.getSimpleName() + " = " + rhs.toString() + ";";
    }

    public String staticString(String className){
        return className + "." + reference.getSimpleName() + " = " + rhs.toString() + ";";
    }
}
