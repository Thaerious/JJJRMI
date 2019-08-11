package ca.frar.jjjrmi.utility;
import ca.frar.jjjrmi.annotations.Generated;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.JJJOptions;
import ca.frar.jjjrmi.annotations.ProcessLevel;
import static ca.frar.jjjrmi.annotations.ProcessLevel.ANNOTATED;
import ca.frar.jjjrmi.annotations.Scope;
import static ca.frar.jjjrmi.annotations.Scope.PUBLIC;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;

public class JJJOptionsHandler {
    private String jsExtends = "";
    private ProcessLevel processLevel = ANNOTATED;
    private Scope scope = PUBLIC;
    private boolean retain = true;
    private String name = "";
    private boolean hasName = false;
    private boolean generateJS = true;
    private boolean hasJJJ = true;
    private boolean generated = false;
    
    public JJJOptionsHandler(Object object) {
        JJJ jjj = object.getClass().getAnnotation(JJJ.class);
        JJJOptions jjjOptions = object.getClass().getAnnotation(JJJOptions.class);
        this.generated = object.getClass().getAnnotation(Generated.class) != null;
        setup(object.getClass().getSimpleName(), jjj, jjjOptions);
    }

    public JJJOptionsHandler(Class<?> aClass) {
        JJJ jjj = aClass.getAnnotation(JJJ.class);
        JJJOptions jjjOptions = aClass.getAnnotation(JJJOptions.class);
        this.generated = aClass.getAnnotation(Generated.class) != null;
        setup(aClass.getSimpleName(), jjj, jjjOptions);
    }

    public JJJOptionsHandler(CtType<?> CtType) {
        JJJ jjj = CtType.getAnnotation(JJJ.class);
        JJJOptions jjjOptions = CtType.getAnnotation(JJJOptions.class);
        this.generated = CtType.getAnnotation(Generated.class) != null;
        setup(CtType.getSimpleName(), jjj, jjjOptions);
    }
    
    private void setup(String name, JJJ jjj, JJJOptions jjjOptions) {
        this.name = name;

        if (jjj == null){
            this.hasJJJ = false;
        } else if (!jjj.value().isEmpty()){
            this.name = jjj.value();
            this.hasName = true;
        }

        if (jjjOptions == null) return;

        this.jsExtends = jjjOptions.jsExtends();
        this.processLevel = jjjOptions.processLevel();
        this.retain = jjjOptions.retain();
        this.generateJS = jjjOptions.generateJS();
    }

    public String getExtends() {
        return this.jsExtends;
    }

    public boolean hasExtends() {
        return !this.jsExtends.equals("");
    }

    public ProcessLevel processLevel() {
        return this.processLevel;
    }

    public boolean isProcessLevel(ProcessLevel processLevel) {
        return this.processLevel == processLevel;
    }

    public Scope scope() {
        return this.scope;
    }

    public boolean isScope(Scope scope) {
        return this.scope == scope;
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

    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("@JJJ(");
        builder.append("jsExtends=").append(jsExtends).append(", ");
        builder.append("processLevel=").append(processLevel).append(", ");
        builder.append("scope=").append(scope).append(", ");
        builder.append("retain=").append(retain).append(", ");
        builder.append("name=").append(name).append(", ");
        builder.append("generateJS=").append(generateJS).append(", ");
        builder.append("hasJJJ=").append(hasJJJ).append(", ");
        builder.append("generated=").append(generated);
        builder.append(")");
        return builder.toString();
    }
}
