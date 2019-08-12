package ca.frar.jjjrmi.jsbuilder.code;
import ca.frar.jjjrmi.annotations.NativeJS;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.reference.CtTypeReference;

public class JSTypeAccess extends AbstractJSCodeElement {
    private final CtTypeReference<?> accessedType;
    private String literal = null;

    public JSTypeAccess(CtTypeAccess<?> ctTypeAccess) {
        accessedType = ctTypeAccess.getAccessedType();
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