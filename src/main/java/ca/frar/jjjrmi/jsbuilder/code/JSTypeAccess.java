package ca.frar.jjjrmi.jsbuilder.code;
import static ca.frar.jjjrmi.Global.LOGGER;
import ca.frar.jjjrmi.annotations.NativeJS;
import java.util.HashSet;
import java.util.Set;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.reference.CtTypeReference;

public class JSTypeAccess extends AbstractJSCodeElement {
    private final CtTypeReference<?> accessedType;
    private String literal = null;

    public JSTypeAccess(CtTypeAccess<?> ctTypeAccess) {
        LOGGER.trace(this.getClass().getSimpleName());
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