package ca.frar.jjjrmi.jsbuilder;
import ca.frar.jjjrmi.utility.JJJOptionsHandler;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.JSPrequel;
import ca.frar.jjjrmi.annotations.JSRequire;
import ca.frar.jjjrmi.jsbuilder.code.JSCodeElement;
import ca.frar.jjjrmi.jsbuilder.code.JSFieldDeclaration;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.annotations.ServerSide;
import ca.frar.jjjrmi.annotations.SkipJS;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.Level;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.AnnotationFilter;
import spoon.reflect.visitor.filter.TypeFilter;

public class JSClassBuilder<T> {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(JSClassBuilder.class);
    
    protected JSHeaderBuilder header = new JSHeaderBuilder();
    protected JSMethodBuilder constructor = new JSMethodBuilder("constructor");
    protected final CtClass<T> ctClass;
    protected final JJJOptionsHandler jjjOptions;
    protected List<JSMethodBuilder> methods = new ArrayList<>();
    protected List<JSFieldDeclaration> staticFields = new ArrayList<>();
    protected List<JSCodeElement> sequel = new ArrayList<>();
    protected JSClassBuilder<?> container = null;
    protected HashMap<String, CtType> requires = new HashMap<>(); // js require statements
    protected ArrayList<JSClassBuilder> nested = new ArrayList<>();

    /* locally defined classes and enums (not fully implemented) */
    JSClassBuilder(CtClass<T> ctClass) {
        this.ctClass = ctClass;
        this.jjjOptions = new JJJOptionsHandler(ctClass);
    }

    public boolean hasContainer() {
        return container != null;
    }

    public JSClassBuilder<?> getContainer() {
        return container;
    }

    CtClass<T> getCtClass() {
        return ctClass;
    }

    protected String getQualifiedName(CtClass<?> ctClass) {
        String orgName = ctClass.getSimpleName();
        ctClass.setSimpleName(jjjOptions.getName());
        String rvalue = ctClass.getQualifiedName();
        ctClass.setSimpleName(orgName);
        return rvalue;
    }

    JSClassBuilder<T> build() {
        LOGGER.debug("JSClassBuilder.build()");
        addJJJMethods();
        setHeader(new JSHeaderBuilder().setName(jjjOptions.getName()));

        /* determine superclass, first by jjjoptions then by extends */
        CtType<?> supertype = null;
        CtTypeReference<?> superclass = ctClass.getSuperclass();
        
        if (superclass != null){
            supertype = ctClass.getSuperclass().getDeclaration();
        }
        
        if (this.jjjOptions.hasExtends()) {
            LOGGER.log(Level.forName("VERBOSE", 455), "Setting superclass based on jjj options: " + jjjOptions.getExtends());
            this.getHeader().setExtend(jjjOptions.getExtends());
        } else if (supertype == null) {
            LOGGER.log(Level.forName("VERY-VERBOSE", 455), "No superclass found.");
        } else if (supertype.hasAnnotation(JJJ.class)) {
            LOGGER.log(Level.forName("VERBOSE", 455), "Setting superclass based on java class: " + supertype.getSimpleName());
            this.getHeader().setExtend(supertype.getSimpleName());
            requires.put(supertype.getSimpleName(), supertype);
        } else {
            LOGGER.log(Level.forName("VERY-VERBOSE", 455), "Direct superclass does not have @JJJ annotation: " + supertype.getSimpleName());
        }

        /* add to require list each constructor call (new) of a top level type with @JJJ */
        ctClass
            .<CtConstructorCall>filterChildren(new TypeFilter<>(CtConstructorCall.class))
            .<CtConstructorCall, CtType>map(ele -> ele.getType().getTypeDeclaration())
            .<CtType>select(ele -> ele.isTopLevel())
            .<CtType>select(new AnnotationFilter<>(JJJ.class))
            .<CtType>forEach((CtType ele) ->{
                JJJOptionsHandler options = new JJJOptionsHandler(ele);
                if (options.getName().equals(ctClass.getSimpleName()) == false){
                    LOGGER.log(Level.forName("VERBOSE", 455), "Required class detected: " + options.getName());
                    requires.put(options.getName(), ele);
                }
            });
        
        TypeFactory Type = ctClass.getFactory().Type();
        
        /* add enum references with @JJJ to the require list */
        ctClass
            .<CtTypeReference>filterChildren(new TypeFilter<>(CtTypeReference.class))
            .<CtTypeReference>select(ele->!ele.getSimpleName().equals(CtTypeReference.NULL_TYPE_NAME))                
            .<CtTypeReference, CtType>map(ele -> ele.getTypeDeclaration())
            .<CtType>select(ele -> ele.isEnum())
            .<CtType>select(ele -> ele.isTopLevel())
            .<CtType>select(new AnnotationFilter<>(JJJ.class))
            .<CtType>forEach((CtType ele) ->{
                JJJOptionsHandler options = new JJJOptionsHandler(ele);
                LOGGER.log(Level.forName("VERBOSE", 455), "Required class detected: " + options.getName());
                requires.put(options.getName(), ele);
            });

        /* Constructor Generation */
        Set<? extends CtConstructor<?>> allConstructors = ctClass.getConstructors();

        /* Keep only constructors that will generate JS */
        ArrayList<CtConstructor<?>> vettedConstructors = new ArrayList<>();
        for (CtConstructor<?> ctConstructor : allConstructors) {
            if (testGenerateMethod(ctClass, ctConstructor)) {
                vettedConstructors.add(ctConstructor);
            }
        }

        if (vettedConstructors.size() > 1) {
            LOGGER.warn("Multiple constructors found: " + ctClass.getQualifiedName());
        }

        if (vettedConstructors.isEmpty()) {
            LOGGER.log(Level.forName("VERBOSE", 455), "No constructor found, generating default.");
            new JSConstructorGenerator(ctClass, this).run();
        } else {
            LOGGER.log(Level.forName("VERBOSE", 455), "Constructors found, generating js constructors.");
            for (CtConstructor<?> ctConstructor : vettedConstructors) {
                new JSConstructorGenerator(ctClass, ctConstructor, this).run();
            }
        }

        /* process all the methods */
        Set<CtMethod<?>> allMethods = ctClass.getMethods();
        for (CtMethod<?> ctMethod : allMethods) {
            if (testGenerateMethod(ctClass, ctMethod)) {
//                if (ctMethod.getAnnotation(ServerSide.class) != null && !ctMethod.hasModifier(ModifierKind.PUBLIC)) {
//                    LOGGER.warn("@ServerSide annotated method '" + ctMethod.getSimpleName() + "' of class '" + ctClass.getQualifiedName() + "' is not public.");
//                }
                new JSMethodGenerator(ctMethod, this).run();
            }
        }

        /* process all inner types (enum only atm) */
        Set<CtType<?>> nestedTypes = ctClass.getNestedTypes();
        for (CtType ctType : nestedTypes) {
            if (!ctType.isEnum()) {
                LOGGER.log(Level.forName("VERY-VERBOSE", 455), "(--)" + ctClass.getSimpleName() + "." + ctType.getSimpleName() + " not an enumeration");
            } else if (ctClass.getAnnotation(JJJ.class) == null) {
                LOGGER.log(Level.forName("VERY-VERBOSE", 455), "(--)" + ctClass.getSimpleName() + "." + ctType.getSimpleName() + " class missing @JJJ");
            } else {
                LOGGER.log(Level.forName("VERBOSE", 455), "(++)" + ctClass.getSimpleName() + "." + ctType.getSimpleName());
                JSEnumBuilder<? extends Enum<?>> jsEnumBuilder = new JSEnumBuilder<>((CtEnum<?>) ctType).build();
                this.nested.add(jsEnumBuilder);
            }
        }

        return this;
    }

    protected boolean hasNativeJS(CtTypeReference<?> ctTypeReference) {
        return ctTypeReference.getAnnotation(NativeJS.class) != null;
    }

    protected boolean testGenerateMethod(CtClass<?> ctClass, CtElement ctElement) {
        boolean skipJS = ctElement.getAnnotation(SkipJS.class) != null;
        boolean nativeJS = ctElement.getAnnotation(NativeJS.class) != null;
        boolean serverSide = ctElement.getAnnotation(ServerSide.class) != null;
        
        if (skipJS) return false;
        if (nativeJS) return true;
        
        switch (jjjOptions.processLevel()) {
            case ALL:
                return true;
            case NONE:                
                return false;            
            default:
                if (serverSide) return true;
                return false;
        }
    }

    void addJJJMethods() {
        JSMethodBuilder jsTransMethod = new JSMethodBuilder();
        jsTransMethod.setStatic(true);
        jsTransMethod.setName("__isTransient");
        if (this.jjjOptions.retain()) {
            jsTransMethod.setBody("return false;"); // note JS asks isTransient = !retain
        } else {
            jsTransMethod.setBody("return true;");
        }
        addMethod(jsTransMethod);

        JSMethodBuilder jsGetClassMethod = new JSMethodBuilder();
        jsGetClassMethod.setStatic(true);
        jsGetClassMethod.setName("__getClass");
        jsGetClassMethod.appendBody("return \"" + this.getFullPath() + "\";");
        addMethod(jsGetClassMethod);

        JSMethodBuilder jsIsEnumMethod = new JSMethodBuilder();
        jsIsEnumMethod.setStatic(true);
        jsIsEnumMethod.setName("__isEnum");
        jsIsEnumMethod.appendBody("return false;");
        addMethod(jsIsEnumMethod);
    }

    public void addStaticField(JSFieldDeclaration field) {
        this.staticFields.add(field);
    }

    public void setHeader(JSHeaderBuilder header) {
        this.header = header;
    }

    public JSHeaderBuilder getHeader() {
        return header;
    }

    public final void addMethod(JSMethodBuilder method) {
        if (method.getName().equals("constructor")) {
            this.constructor = method;
        } else {
            this.methods.add(method);
        }
    }

    public JSMethodBuilder getMethod(String name) {
        if (name.equals("constructor")) {
            return this.constructor;
        }
        for (JSMethodBuilder builder : this.methods) {
            if (builder.getName().equals(name)) {
                return builder;
            }
        }
        return null;
    }

    public void addSequel(JSCodeElement jsCodeElement) {
        this.sequel.add(jsCodeElement);
    }

    public String fullString() throws JSBuilderException {
        if (header == null) {
            throw new JSBuilderIncompleteObjectException("class", "header");
        }
        
        StringBuilder builder = new StringBuilder();
        builder.append("\"use strict\";\n");
        
        List<CtAnnotation<? extends Annotation>> annotations = ctClass.getAnnotations();
        for (CtAnnotation ctAnnotation : annotations) {
            Annotation actualAnnotation = ctAnnotation.getActualAnnotation();
            if (actualAnnotation instanceof JSRequire) {
                appendRequire(builder, (JSRequire) actualAnnotation);
            }
            else if (actualAnnotation instanceof JSPrequel) {
                appendPrequel(builder, (JSPrequel) actualAnnotation);
            }            
        }

        for (CtType anImport : this.requires.values()) {
            builder.append("const ");
            builder.append(JSClassBuilder.requireString(anImport));
            builder.append("\n");
        }

        builder.append(header.fullString(this.jjjOptions.getName()));
        builder.append(bodyString());

        for (JSClassBuilder nestedClassBuilder : this.nested) {
            builder.append(nestedClassBuilder.fullString());
            builder.append(this.jjjOptions.getName()).append(".").append(nestedClassBuilder.getSimpleName()).append(" = ").append(nestedClassBuilder.getSimpleName()).append(";");
        }

        builder.append("\nif (typeof module !== \"undefined\") module.exports = ");
        builder.append(this.ctClass.getSimpleName());
        builder.append(";");
        
        return builder.toString();
    }

    private void appendRequire(StringBuilder builder, JSRequire jsRequire) {
        LOGGER.log(Level.forName("VERBOSE", 455), "@JSRequire: " + jsRequire.name());
        builder.append("const ");
        builder.append(jsRequire.name());
        builder.append(" = require(\"");
        builder.append(jsRequire.value());
        builder.append("\");\n");
    }
    
    private void appendPrequel(StringBuilder builder, JSPrequel jsPrequel){
        LOGGER.log(Level.forName("VERBOSE", 455), "@JSPrequel: " + jsPrequel.value());        
        builder.append(jsPrequel.value());
        builder.append("\n");        
    }
    
    public String bodyString() throws JSBuilderException {
        StringBuilder builder = new StringBuilder();
        builder.append(" {\n");
        builder.append(constructor.fullString()).append("\n");
        for (JSMethodBuilder method : methods) {
            builder.append(method.fullString()).append("\n");
        }
        builder.append("};\n");
        builder.append(sequelString());
//        for (JSFieldDeclaration field : staticFields) {            
//            builder.append(field.staticString(ctClass.getSimpleName())).append("\n");
//        }
        return builder.toString();
    }

    public String sequelString() throws JSBuilderException {
        StringBuilder builder = new StringBuilder();
        for (JSCodeElement element : sequel) {
            builder.append(element.toString());
            builder.append("\n");
        }
        return builder.toString();
    }

    public String getFullPath() {
        return this.ctClass.getPackage().getQualifiedName() + "." + this.jjjOptions.getName();
    }

    public String getSimpleName() {
        return this.jjjOptions.getName();
    }

    public String getQualifiedName() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.jjjOptions.getName());
        JSClassBuilder<?> current = this.getContainer();
        while (current != null) {
            builder.insert(0, ".");
            builder.insert(0, current.jjjOptions.getName());
            current = current.getContainer();
        }
        return builder.toString();
    }

    public static String requireString(CtType source) {
        StringBuilder builder = new StringBuilder();
        builder.append(new JJJOptionsHandler(source).getName());
        builder.append(" = require(\"./");

        List<CtType> nestedChain = nestedChain(source);
        builder.append(new JJJOptionsHandler(nestedChain.remove(0)).getName());
        builder.append("\")");
        while (!nestedChain.isEmpty()) {
            builder.append(".");
            builder.append(new JJJOptionsHandler(nestedChain.remove(0)).getName());
        }
        builder.append(";");
        return builder.toString();
    }

    private static List<CtType> nestedChain(CtType source) {
        ArrayList<CtType> list = new ArrayList<>();
        CtType current = source;
        list.add(0, current);
        while (!current.isTopLevel()) {
            current = current.<CtClass>getParent(CtClass.class);
            list.add(0, current);
        }
        return list;
    }
}