package ca.frar.jjjrmi.jsbuilder.code;

import java.util.Set;
import spoon.reflect.declaration.CtType;

public interface JSCodeElement {
    boolean isEmpty();
    String scoped();
    Set<CtType> getRequires();
    String toXML(int indent);
}