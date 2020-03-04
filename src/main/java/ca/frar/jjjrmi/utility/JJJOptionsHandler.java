package ca.frar.jjjrmi.utility;
import static ca.frar.jjjrmi.Global.LOGGER;
import ca.frar.jjjrmi.annotations.DoNotPackage;
import ca.frar.jjjrmi.annotations.Handles;
import ca.frar.jjjrmi.annotations.JJJ;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import ca.frar.jjjrmi.annotations.IsSocket;
import ca.frar.jjjrmi.socket.JJJObject;
import ca.frar.jjjrmi.translator.AHandler;
import spoon.reflect.reference.CtTypeReference;

public class JJJOptionsHandler {

    private boolean isHandler;
    private IsSocket isSocket;
    private final JJJ jjj;
    private String jsExtends = "";
    private boolean retain = true;
    private String name = "";
    private boolean hasName = false;
    private boolean generateJS = true;
    private boolean generated = false;
    private DoNotPackage doNotPackage;

    public JJJOptionsHandler(Object object) {
        jjj = object.getClass().getAnnotation(JJJ.class);
        name = object.getClass().getSimpleName();
        isSocket = object.getClass().getAnnotation(IsSocket.class);
        doNotPackage = object.getClass().getAnnotation(DoNotPackage.class);
        isHandler = object.getClass().getAnnotation(Handles.class) != null;
    }

    public JJJOptionsHandler(Class<?> aClass) {
        jjj = aClass.getAnnotation(JJJ.class);
        name = aClass.getSimpleName();
        isSocket = aClass.getAnnotation(IsSocket.class);
        doNotPackage = aClass.getAnnotation(DoNotPackage.class);
        isHandler = aClass.getAnnotation(Handles.class) != null;
    }

    public JJJOptionsHandler(CtType<?> ctType) {
        jjj = ctType.getAnnotation(JJJ.class);
        name = ctType.getSimpleName();
        isSocket = ctType.getAnnotation(IsSocket.class);
        doNotPackage = ctType.getAnnotation(DoNotPackage.class);
        
        CtTypeReference<AHandler> hndRef = ctType.getFactory().Type().createReference(AHandler.class);
        isHandler = ctType.getAnnotation(Handles.class) != null;
    }

    public JJJOptionsHandler(CtTypeReference<?> ctTypeReference) {
        if (ctTypeReference.getTypeDeclaration() != null) {
            CtType<?> ctType = ctTypeReference.getTypeDeclaration();
            jjj = ctType.getAnnotation(JJJ.class);
            name = ctType.getSimpleName();
            isSocket = ctType.getAnnotation(IsSocket.class);
            
            CtTypeReference<AHandler> hndRef = ctType.getFactory().Type().createReference(AHandler.class);
            isHandler = ctType.isSubtypeOf(hndRef);            
        } else {
            LOGGER.warn("Unknown type (missing classpath?): " + ctTypeReference.getSimpleName());
            jjj = ctTypeReference.getAnnotation(JJJ.class);
            name = ctTypeReference.getSimpleName();
            isSocket = null;            
        }
    }

    /**
     * The object that the JS object will extend.
     * @return
     */
    public String getExtends() {
        return jjj.jsExtends();
    }

    /**
     * Do not add class to packageFile.js
     * @return 
     */
    public boolean doNotPackage(){
        return this.doNotPackage != null;
    }
    
    public boolean hasExtends() {
        return jjj != null && !this.jsExtends.isEmpty();
    }

    public boolean isHandler() {
        return this.isHandler;
    }

    public boolean topLevel() {
        return jjj != null && jjj.topLevel();
    }

    /**
     * Set insertJJJMethods in @JJJ to false to not insert handling methods.
     * @return 
     */
    public boolean insertJJJMethods() {
        return jjj == null || jjj.insertJJJMethods();
    }

    public boolean retain() {
        if (jjj == null) return true;
        return jjj.retain();
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

    /**
     * True is this object has the @JJJ annotation.
     * @return 
     */
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
