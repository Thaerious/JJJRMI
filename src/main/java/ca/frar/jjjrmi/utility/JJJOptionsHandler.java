package ca.frar.jjjrmi.utility;

import ca.frar.jjjrmi.annotations.JJJ;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import ca.frar.jjjrmi.annotations.IsSocket;
import spoon.reflect.reference.CtTypeReference;

public class JJJOptionsHandler {

    private IsSocket isSocket;
    private final JJJ jjj;
    private String jsExtends = "";
    private boolean retain = true;
    private String name = "";
    private boolean hasName = false;
    private boolean generateJS = true;
    private boolean generated = false;

    public JJJOptionsHandler(Object object) {
        jjj = object.getClass().getAnnotation(JJJ.class);
        name = object.getClass().getSimpleName();
        isSocket = object.getClass().getAnnotation(IsSocket.class);
    }

    public JJJOptionsHandler(Class<?> aClass) {
        jjj = aClass.getAnnotation(JJJ.class);
        name = aClass.getSimpleName();
        isSocket = aClass.getAnnotation(IsSocket.class);
    }

    public JJJOptionsHandler(CtType<?> ctType) {
        jjj = ctType.getAnnotation(JJJ.class);
        name = ctType.getSimpleName();
        isSocket = ctType.getAnnotation(IsSocket.class);
    }

    public JJJOptionsHandler(CtTypeReference<?> ctTypeReference) {
        if (ctTypeReference.getDeclaration() != null) {
            CtType<?> ctType = ctTypeReference.getDeclaration();
            jjj = ctType.getAnnotation(JJJ.class);
            name = ctType.getSimpleName();
            isSocket = ctType.getAnnotation(IsSocket.class);
        } else {
            jjj = null;
            name = ctTypeReference.getSimpleName();
            isSocket = null;            
        }
    }

    /**
     * The object that the JS object will extend.
     *
     * @return
     */
    public String getExtends() {
        return jjj.jsExtends();
    }

    public boolean hasExtends() {
        return jjj != null && !this.jsExtends.isEmpty();
    }

    public boolean topLevel() {
        return jjj != null && jjj.topLevel();
    }

    public boolean insertJJJMethods() {
        return jjj == null || jjj.insertJJJMethods();
    }

    public boolean retain() {
        return jjj == null || jjj.retain();
    }

    public boolean hasName() {
        return jjj != null && !jjj.name().isEmpty();
    }

    public String getName() {
        if (hasName()) return jjj.name();
        return name;
    }

    public static String getName(CtClass<?> ctClass) {
        return new JJJOptionsHandler(ctClass).getName();
    }

    public boolean generateJS() {
        return jjj == null || jjj.generateJS();
    }

    public boolean hasJJJ() {
        return this.jjj != null;
    }

    public boolean isSocket() {
        return this.isSocket != null;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("@JJJ(");
        builder.append("jsExtends=").append(jsExtends).append(", ");
        builder.append("retain=").append(retain).append(", ");
        builder.append("name=").append(name).append(", ");
        builder.append("generateJS=").append(generateJS).append(", ");
        builder.append("generated=").append(generated);
        builder.append(")");
        return builder.toString();
    }
}
