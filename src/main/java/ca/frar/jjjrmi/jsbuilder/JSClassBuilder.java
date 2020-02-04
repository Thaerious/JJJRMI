package ca.frar.jjjrmi.jsbuilder;

import static ca.frar.jjjrmi.Global.LOGGER;
import static ca.frar.jjjrmi.Global.VERBOSE;
import static ca.frar.jjjrmi.Global.VERY_VERBOSE;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.JSPrequel;
import ca.frar.jjjrmi.annotations.JSRequire;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.annotations.SkipJS;
import ca.frar.jjjrmi.exceptions.TypeDeclarationNotFoundWarning;
import ca.frar.jjjrmi.jsbuilder.code.JSCodeElement;
import ca.frar.jjjrmi.jsbuilder.code.JSFieldDeclaration;
import ca.frar.jjjrmi.socket.JJJObject;
import ca.frar.jjjrmi.utility.JJJOptionsHandler;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

public class JSClassBuilder<T> {

    protected JSHeaderBuilder header = new JSHeaderBuilder();
    protected JSMethodBuilder constructor = new JSMethodBuilder("constructor");
    protected final CtClass<T> ctClass;
    protected final JJJOptionsHandler jjjOptions;
    protected List<JSMethodBuilder> methods = new ArrayList<>();
    protected List<JSFieldDeclaration> staticFields = new ArrayList<>();
    protected List<JSCodeElement> sequel = new ArrayList<>();
    protected JSClassBuilder<?> container = null;
    protected HashSet<CtTypeReference> requireSet = new HashSet<>(); // js require statements
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

    JSClassBuilder<T> build() {
        LOGGER.trace("JSClassBuilder.build()");
        addJJJMethods();
        setHeader(new JSHeaderBuilder().setName(jjjOptions.getName()));

        /* determine superclass, first by jjjoptions then by extends */
        CtType<?> supertype = null;
        CtTypeReference<?> superclass = ctClass.getSuperclass();

        if (superclass != null) {
            supertype = ctClass.getSuperclass().getDeclaration();
        }

        if (this.jjjOptions.hasExtends()) {
            LOGGER.log(VERBOSE, "Setting JS superclass from @JJJ annotation: " + jjjOptions.getExtends());
            this.getHeader().setExtend(jjjOptions.getExtends());
        } else if (supertype == null) {
            LOGGER.log(VERY_VERBOSE, "No JS superclass found.");
        } else if (supertype.getSimpleName().equals(JJJObject.class.getSimpleName())) {
            LOGGER.log(VERY_VERBOSE, "Direct Java superclass is of type JJJObject: " + supertype.getSimpleName());
        } else {
            LOGGER.log(VERBOSE, "Setting JS superclass from Java superclass: " + supertype.getSimpleName());
            this.getHeader().setExtend(supertype.getSimpleName());
            requireSet.add(supertype.getReference());
        }

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

        /* Create constructor, if none found create default */
        if (vettedConstructors.isEmpty()) {
            LOGGER.log(VERBOSE, "No constructor found, generating default.");
            new JSConstructorGenerator(ctClass, this).run();
        } else {
            LOGGER.log(VERBOSE, "Constructors found, generating js constructors.");
            for (CtConstructor<?> ctConstructor : vettedConstructors) {
                JSMethodBuilder jsMethodBuilder = new JSMethodGenerator("constructor", ctConstructor, ctConstructor).run();
                this.constructor = jsMethodBuilder;

                try {
                    this.requireSet.addAll(jsMethodBuilder.getRequires());
                } catch (TypeDeclarationNotFoundWarning ex) {
                    ex.setClass(this.getQualifiedName());
                    throw ex;
                }
            }
        }

        /* process all the methods */
        Set<CtMethod<?>> allMethods = ctClass.getMethods();
        for (CtMethod<?> ctMethod : allMethods) {
            if (testGenerateMethod(ctClass, ctMethod)) {
                JSMethodBuilder jsMethodBuilder = new JSMethodGenerator(ctMethod.getSimpleName(), ctMethod, ctMethod).run();
                this.methods.add(jsMethodBuilder);
                this.requireSet.addAll(jsMethodBuilder.getRequires());
            }
        }

        /* process all inner types (enum only atm) */
        Set<CtType<?>> nestedTypes = ctClass.getNestedTypes();
        for (CtType ctType : nestedTypes) {
            if (!ctType.isEnum()) {
                LOGGER.log(VERY_VERBOSE, "(--)" + ctClass.getSimpleName() + "." + ctType.getSimpleName() + " not an enumeration");
            } else if (ctClass.getAnnotation(JJJ.class) == null) {
                LOGGER.log(VERY_VERBOSE, "(--)" + ctClass.getSimpleName() + "." + ctType.getSimpleName() + " class missing @JJJ");
            } else {
                LOGGER.log(VERBOSE, "(++)" + ctClass.getSimpleName() + "." + ctType.getSimpleName());
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

        if (skipJS) {
            return false;
        }
        if (nativeJS) {
            return true;
        }
        return false;
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
        jsGetClassMethod.appendToBody("return \"" + this.getFullPath() + "\";");
        addMethod(jsGetClassMethod);

        JSMethodBuilder jsIsEnumMethod = new JSMethodBuilder();
        jsIsEnumMethod.setStatic(true);
        jsIsEnumMethod.setName("__isEnum");
        jsIsEnumMethod.appendToBody("return false;");
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
            } else if (actualAnnotation instanceof JSPrequel) {
                appendPrequel(builder, (JSPrequel) actualAnnotation);
            }
        }

        for (CtTypeReference anImport : this.requireSet) {
            if (anImport.getTypeDeclaration() == this.getCtClass()) continue;
            if (anImport.isEnum() && new JJJOptionsHandler(anImport.getDeclaration()).hasJJJ()) continue;
            
            CtType<?> importType = ctClass.getSuperclass().getDeclaration();
            if (importType == null && !checkExternalType(anImport.getSimpleName())) {
                LOGGER.warn("unknown type in " + this.getSimpleName() + ": " + anImport.getSimpleName());
                continue;
            }

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
        builder.append(";\n");

        return builder.toString();
    }

    /**
     * Check if a require has been registered for this class.
     *
     * @return
     */
    private boolean checkExternalType(String name) {
        for (CtAnnotation<?> ctAnnotation : ctClass.getAnnotations()) {
            Annotation actualAnnotation = ctAnnotation.getActualAnnotation();
            if (actualAnnotation instanceof JSRequire) {                
                JSRequire jsRequire = (JSRequire) actualAnnotation;
                if (jsRequire.name().equals(name)) return true;
            }
        }
        return false;
    }

    private void appendRequire(StringBuilder builder, JSRequire jsRequire) {
        builder.append("const ");
        builder.append(jsRequire.name());
        builder.append(" = require(\"");
        builder.append(jsRequire.value());
        builder.append("\")");
        if (!jsRequire.postfix().isEmpty()) {
            builder.append(".").append(jsRequire.postfix());
        }
        builder.append(";\n");
    }

    private void appendPrequel(StringBuilder builder, JSPrequel jsPrequel) {
        LOGGER.log(VERBOSE, "@JSPrequel: " + jsPrequel.value());
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

    public static String requireString(CtTypeReference ref) {
        CtType source = ref.getTypeDeclaration();

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

    public String toXML(int indent) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < indent; i++) builder.append("\t");

        builder.append("<").append(this.getClass().getSimpleName());
        builder.append(" name=\"").append(this.getQualifiedName()).append("\"");
        builder.append(">\n");

        builder.append(constructor.toXML(indent + 1));
        for (JSMethodBuilder method : this.methods) {
            builder.append(method.toXML(indent + 1));
        }

        for (int i = 0; i < indent; i++) builder.append("\t");
        builder.append("</").append(this.getClass().getSimpleName()).append(">\n");

        return builder.toString();
    }
}
