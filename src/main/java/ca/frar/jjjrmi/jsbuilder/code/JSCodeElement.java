package ca.frar.jjjrmi.jsbuilder.code;

import java.util.Set;
import spoon.reflect.reference.CtTypeReference;

public interface JSCodeElement {
    boolean noChildren();
    String scoped();
    Set<CtTypeReference> getRequires();
    String toXML(int indent);
    
    /**
     * Retrieve a JSElement to insert into tree, usually just 'this'.
     * Override if processing may return different types.
     * @return 
     */
    JSCodeElement get(); 
}