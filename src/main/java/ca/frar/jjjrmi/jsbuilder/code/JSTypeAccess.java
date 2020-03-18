package ca.frar.jjjrmi.jsbuilder.code;
import static ca.frar.jjjrmi.Global.LOGGER;
import ca.frar.jjjrmi.annotations.NativeJS;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.reference.CtTypeReference;

public class JSTypeAccess extends AbstractJSCodeElement {
    private final CtTypeReference<?> accessedType;
    private final CtTypeAccess<?> ctTypeAccess;
    private String literal = null;    

    public JSTypeAccess(CtTypeAccess<?> ctTypeAccess) {
        LOGGER.trace(this.getClass().getSimpleName());
        this.ctTypeAccess = ctTypeAccess;
        accessedType = ctTypeAccess.getAccessedType();
    }

    @Override
    public Set<CtTypeReference> getRequires() {
        HashSet<CtTypeReference> requires = new HashSet<>();
        requires.addAll(super.getRequires());
        requires.add(accessedType);          
        return requires;
    }
    
    @Override
    public String toXML(int indent) {
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put("name", "" + ctTypeAccess.getAccessedType().getSimpleName());        
        return toXML(indent, attributes);
    }    
    
    @Override
    public String toString() {
        String name = accessedType.getQualifiedName();
        CtTypeReference<?> topLevelType = accessedType.getTopLevelType();

        NativeJS nativeJS = topLevelType.getAnnotation(NativeJS.class);
        if (nativeJS != null && !nativeJS.value().isEmpty()) return nativeJS.value();

        String topLevelName = topLevelType.getQualifiedName();
        name = name.replace(topLevelName, topLevelType.getSimpleName());
        name = name.replaceAll("[$]", ".");
        return name;
    }
}