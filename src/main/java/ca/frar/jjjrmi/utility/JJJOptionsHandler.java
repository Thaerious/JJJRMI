package ca.frar.jjjrmi.utility;
import ca.frar.jjjrmi.annotations.Generated;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.ProcessLevel;
import static ca.frar.jjjrmi.annotations.ProcessLevel.ANNOTATED;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import ca.frar.jjjrmi.annotations.IsSocket;

public class JJJOptionsHandler {
    private String jsExtends = "";
    private ProcessLevel processLevel = ANNOTATED;
    private boolean retain = true;
    private String name = "";
    private boolean hasName = false;
    private boolean generateJS = true;
    private boolean hasJJJ = true;
    private boolean generated = false;
    private boolean isSocket = false;
    
    public JJJOptionsHandler(Object object) {
        JJJ jjj = object.getClass().getAnnotation(JJJ.class);        
        this.generated = object.getClass().getAnnotation(Generated.class) != null;
        setup(object.getClass().getSimpleName(), jjj);
        if (object.getClass().getAnnotation(IsSocket.class) != null) this.isSocket = true;
    }

    public JJJOptionsHandler(Class<?> aClass) {
        JJJ jjj = aClass.getAnnotation(JJJ.class);
        this.generated = aClass.getAnnotation(Generated.class) != null;
        setup(aClass.getSimpleName(), jjj);
        if (aClass.getAnnotation(IsSocket.class) != null) this.isSocket = true;
    }

    public JJJOptionsHandler(CtType<?> CtType) {
        JJJ jjj = CtType.getAnnotation(JJJ.class);
        this.generated = CtType.getAnnotation(Generated.class) != null;
        setup(CtType.getSimpleName(), jjj);        
        if (CtType.getAnnotation(IsSocket.class) != null) this.isSocket = true;
    }
    
    private void setup(String name, JJJ jjj) {
        this.name = name;

        if (jjj == null){
            this.hasJJJ = false;
            return;
        } else if (!jjj.name().isEmpty()){
            this.name = jjj.name();
            this.hasName = true;
        }

        this.jsExtends = jjj.jsExtends();
        this.processLevel = jjj.processLevel();
        this.retain = jjj.retain();
        this.generateJS = jjj.generateJS();
    }
    
    /**
     * The object that the JS object will extend.
     * @return 
     */
    public String getExtends() {
        return this.jsExtends;
    }

    public boolean hasExtends() {
        return !this.jsExtends.isEmpty();
    }

    public ProcessLevel processLevel() {
        return this.processLevel;
    }

    public boolean isProcessLevel(ProcessLevel processLevel) {
        return this.processLevel == processLevel;
    }

    public boolean retain() {
        return this.retain;
    }

    public boolean hasName(){
        return this.hasName;
    }

    public String getName() {
        return this.name;
    }

    public static String getName(CtClass<?> ctClass){
        return new JJJOptionsHandler(ctClass).getName();
    }
    
    public boolean generateJS() {
        return this.generateJS;
    }

    public boolean isGenerated(){
        return this.generated;
    }

    public boolean hasJJJ(){
        return this.hasJJJ;
    }

    public boolean isSocket(){
        return this.isSocket;
    }
    
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("@JJJ(");
        builder.append("jsExtends=").append(jsExtends).append(", ");
        builder.append("processLevel=").append(processLevel).append(", ");
        builder.append("retain=").append(retain).append(", ");
        builder.append("name=").append(name).append(", ");
        builder.append("generateJS=").append(generateJS).append(", ");
        builder.append("hasJJJ=").append(hasJJJ).append(", ");
        builder.append("generated=").append(generated);
        builder.append(")");
        return builder.toString();
    }
}
