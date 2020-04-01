package ca.frar.jjjrmi.jsbuilder;

import java.util.Set;
import spoon.reflect.reference.CtTypeReference;

public interface JSCodeElement {
    boolean noChildren();
    boolean hasChildren();
    String scoped();
    Set<CtTypeReference> getRequires();
    String toXML(int indent);
    void setAttr(String key, Object value);
    <T> T getAttr(String key);
    boolean hasAttr(String key);
    
    /**
     * Retrieve a JSElement to insert into tree, usually just 'this'.
     * Override if processing may return different types.
     * @return 
     */
    JSCodeElement get(); 
}