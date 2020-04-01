package ca.frar.jjjrmi.jsbuilder.code;
import ca.frar.jjjrmi.jsbuilder.JSCodeElement;
import ca.frar.jjjrmi.jsbuilder.AbstractJSCodeElement;
import static ca.frar.jjjrmi.Global.LOGGER;
import java.util.HashMap;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtTypeReference;

public class JSAssignment extends AbstractJSCodeElement {
    private final CtAssignment ctAssignment;
    private final JSCodeElement rhs;
    private final JSCodeElement lhs;

    public JSAssignment(CtAssignment ctAssignment) {
        LOGGER.trace(this.getClass().getSimpleName());
        this.ctAssignment = ctAssignment;
        lhs = this.generate(ctAssignment.getAssigned());
        rhs = this.generate(ctAssignment.getAssignment());
    }

    @Override
    public String toString(){
        CtTypeReference<?> lhsRef = ctAssignment.getAssigned().getType();
        CtTypeReference<?> rhsRef = ctAssignment.getAssignment().getType();
        CtTypeReference<Character> CHARACTER_PRIMITIVE = lhsRef.getFactory().Type().CHARACTER_PRIMITIVE;
        
        CtType<?> lhsDec = lhsRef.getTypeDeclaration();
        CtType<?> charDec = CHARACTER_PRIMITIVE.getTypeDeclaration();

        if (lhsDec == charDec){
            return lhsCharToString(rhsRef);
        } else {
            return lhs.toString() + " = " + rhs.toString();
        }
    }
    
    private String lhsCharToString(CtTypeReference<?> rhsRef){
        CtType<?> rht = rhsRef.getTypeDeclaration(); // right hand type
        TypeFactory tf = rhsRef.getFactory().Type(); //type factory

        LOGGER.debug(rhsRef);
        LOGGER.debug(rht);
        
        if (   rht == tf.INTEGER.getTypeDeclaration() 
            || rht == tf.INTEGER_PRIMITIVE.getTypeDeclaration()
            || rht == tf.SHORT.getTypeDeclaration()
            || rht == tf.SHORT_PRIMITIVE.getTypeDeclaration()
            || rht == tf.LONG.getTypeDeclaration()
            || rht == tf.LONG_PRIMITIVE.getTypeDeclaration()                
           ){
            return String.format("%s = String.fromCharCode(%s);", lhs.toString(), rhs.toString());
        }
        if (rht == tf.STRING.getTypeDeclaration()){
            return String.format("%s = %s.charAt(0);", lhs.toString(), rhs.toString());
        }        
        return lhs.toString() + " = " + rhs.toString();
    }
    
    public String toXML(int indent) {
        this.setAttr("name", lhs.toString());
        return super.toXML(indent);
    }

}
