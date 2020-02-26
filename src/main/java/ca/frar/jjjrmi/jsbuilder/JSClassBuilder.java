package ca.frar.jjjrmi.jsbuilder;

import static ca.frar.jjjrmi.Global.LOGGER;
import static ca.frar.jjjrmi.Global.VERBOSE;
import static ca.frar.jjjrmi.Global.VERY_VERBOSE;
import ca.frar.jjjrmi.annotations.InvokeSuper;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.JSPrequel;
import ca.frar.jjjrmi.annotations.JSRequire;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.annotations.ServerSide;
import ca.frar.jjjrmi.annotations.Transient;
import ca.frar.jjjrmi.exceptions.TypeDeclarationNotFoundWarning;
import ca.frar.jjjrmi.jsbuilder.code.JSCodeElement;
import ca.frar.jjjrmi.jsbuilder.code.JSCodeSnippet;
import ca.frar.jjjrmi.jsbuilder.code.JSElementList;
import ca.frar.jjjrmi.jsbuilder.code.JSFieldDeclaration;
import ca.frar.jjjrmi.jsbuilder.code.JSSuperConstructor;
import ca.frar.jjjrmi.socket.JJJObject;
import ca.frar.jjjrmi.utility.JJJOptionsHandler;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.Level;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;

public class JSClassBuilder<T> {

    protected JSHeaderBuilder header = new JSHeaderBuilder();
    protected JSMethodBuilder constructor = new JSMethodBuilder("constructor");
    protected final CtClass<T> ctClass;
    protected final JJJOptionsHandler jjjOptions;
    protected List<JSMethodBuilder> methods = new ArrayList<>();
    protected List<CtField<?>> staticFields = new ArrayList<>();
    protected List<JSCodeElement> sequel = new ArrayList<>();
    protected JSClassBuilder<?> container = null;
    protected HashSet<JSRequire> requireSet = new HashSet<>(); // js require statements
    protected HashSet<CtTypeReference> classRequires = new HashSet<>(); // js require statements
    protected ArrayList<JSClassBuilder> nested = new ArrayList<>();

    /* locally defined classes and enums (not fully implemented) */
    JSClassBuilder(CtClass<T> ctClass) {
        this.ctClass = ctClass;
        this.jjjOptions = new JJJOptionsHandler(ctClass);
    }

    public JJJOptionsHandler getOptions() {
        return jjjOptions;
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

        this.setHeader(new JSHeaderBuilder().setName(jjjOptions.getName()));
        this.addJJJMethods();
        this.buildExtendsSuper();
        this.buildInitMethod();
        this.buildConstructor();
        this.buildAllMethods();
        this.buildInnerTypes();
        this.constructStaticFields();
        this.reportReferences();
        this.convertClassRequires();
        this.extractAnnotatedRequires();

        return this;
    }

    protected void reportReferences() {
        for (JSRequire jsRequire : this.requireSet) {
            LOGGER.log(VERY_VERBOSE, "jsRequire: " + jsRequire.name());
        }
        LOGGER.log(VERY_VERBOSE, this.requireSet.size() + " reference" + (this.requireSet.size() == 1 ? "" : "s") + " found");
    }

    protected void buildInnerTypes() {
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
    }

    /* process all the methods */
    protected void buildAllMethods() {
        Set<CtMethod<?>> allMethods = ctClass.getMethods();
        for (CtMethod<?> ctMethod : allMethods) {
            if (testGenerateMethod(ctClass, ctMethod)) {
                LOGGER.log(VERBOSE, "adding method: " + ctMethod.getSimpleName());
                JSMethodBuilder jsMethodBuilder = new JSMethodGenerator(ctMethod.getSimpleName(), ctMethod, ctMethod).run();
                this.methods.add(jsMethodBuilder);
                this.classRequires.addAll(jsMethodBuilder.getRequires());
            } else {
                LOGGER.log(VERY_VERBOSE, "skipping method: " + ctMethod.getSimpleName());
            }
        }
    }

    protected void buildExtendsSuper() {
        /* determine superclass, first by jjjoptions then by extends */
        CtTypeReference<?> superclass = ctClass.getSuperclass();

        if (this.jjjOptions.hasExtends()) {
            LOGGER.log(VERBOSE, "Setting JS superclass from @JJJ annotation: " + jjjOptions.getExtends());
            this.getHeader().setExtend(jjjOptions.getExtends());
            return;
        }

        if (superclass == null) {
            LOGGER.log(VERY_VERBOSE, "No superclass found.");
            return;
        }

        CtTypeReference<JJJObject> jjjObjectRef = ctClass.getFactory().Type().createReference(JJJObject.class);

        boolean isSubtype = superclass.isSubtypeOf(jjjObjectRef);
        boolean isType = superclass.getTypeDeclaration() == jjjObjectRef.getTypeDeclaration();
        boolean hasAnno = new JJJOptionsHandler(superclass).hasJJJ();

        if (isType) {
            LOGGER.log(VERY_VERBOSE, "Super is JJJObject: " + superclass.getSimpleName());
        } else if (isSubtype) {
            LOGGER.log(VERBOSE, "Super is subtype of JJJObject : " + superclass.getSimpleName());
            this.getHeader().setExtend(superclass.getSimpleName());
            this.addRequire(superclass.getSimpleName(), "./" + superclass.getSimpleName(), "");
        } else if (hasAnno) {
            LOGGER.log(VERY_VERBOSE, "Super has @JJJ: " + superclass.getSimpleName());
            this.getHeader().setExtend(superclass.getSimpleName());
            this.addRequire(superclass.getSimpleName(), "./" + superclass.getSimpleName(), "");
        } else {
            LOGGER.log(VERY_VERBOSE, "Super not subtype of JJJObject and is not annotated: " + superclass.getSimpleName());
        }
    }

    protected void buildConstructor() {
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
            JSMethodBuilder jsMethodBuilder = new JSMethodBuilder("constructor");
            this.constructor = jsMethodBuilder;
        } else {
            LOGGER.log(VERBOSE, "Constructors found, generating js constructors.");
            for (CtConstructor<?> ctConstructor : vettedConstructors) {
                JSMethodBuilder jsMethodBuilder = new JSMethodGenerator("constructor", ctConstructor, ctConstructor).run();
                this.constructor = jsMethodBuilder;
                try {
                    this.classRequires.addAll(jsMethodBuilder.getRequires());
                } catch (TypeDeclarationNotFoundWarning ex) {
                    ex.setClass(this.getQualifiedName());
                    throw ex;
                }
            }
        }
        this.classRequires.addAll(this.constructor.getRequires());

        /* test for and insert super call */
        boolean requiresSuper = false;
        JSCodeSnippet snippet = new JSCodeSnippet("this.__init()");

        int indexOfSuper = this.constructor.getBody().firstIndexOf(JSSuperConstructor.class);

        LOGGER.debug(indexOfSuper);

        if (this.ctClass.getAnnotation(InvokeSuper.class) != null || this.getHeader().hasExtend()) {
            requiresSuper = true;
        }

        if (indexOfSuper != -1 && !requiresSuper) {
            LOGGER.log(VERY_VERBOSE, "Removing super & inserting init");
            this.constructor.getBody().remove(indexOfSuper);
            this.constructor.getBody().add(0, snippet);
        } else if (indexOfSuper == -1 && requiresSuper) {
            LOGGER.log(VERY_VERBOSE, "Inserting super & init");
            this.constructor.getBody().add(0, new JSSuperConstructor());
            this.constructor.getBody().add(1, snippet);
        } else {
            LOGGER.log(VERY_VERBOSE, "Inserting init invocation");
            this.constructor.getBody().add(indexOfSuper + 1, snippet);
        }
    }

    protected boolean hasNativeJS(CtTypeReference<?> ctTypeReference) {
        return ctTypeReference.getAnnotation(NativeJS.class) != null;
    }

    protected boolean testGenerateMethod(CtClass<?> ctClass, CtElement ctElement) {
        boolean nativeJS = ctElement.getAnnotation(NativeJS.class) != null;
        boolean serverSide = ctElement.getAnnotation(ServerSide.class) != null;
        return serverSide || nativeJS;
    }

    protected void addJJJMethods() {
        if (!jjjOptions.insertJJJMethods()) return;
        JSMethodBuilder jsTransMethod = new JSMethodBuilder();
        jsTransMethod.setStatic(true);
        jsTransMethod.setName("__isTransient");
        if (this.jjjOptions.retain()) {
            jsTransMethod.getBody().add(new JSCodeSnippet("return false;"));// note JS asks isTransient = !retain
        } else {
            jsTransMethod.getBody().add(new JSCodeSnippet("return true;"));
        }
        addMethod(jsTransMethod);

        JSMethodBuilder jsGetClassMethod = new JSMethodBuilder();
        jsGetClassMethod.setStatic(true);
        jsGetClassMethod.setName("__getClass");
        jsGetClassMethod.getBody().add(new JSCodeSnippet("return \"" + this.getFullPath() + "\";"));
        addMethod(jsGetClassMethod);

        JSMethodBuilder jsIsEnumMethod = new JSMethodBuilder();
        jsIsEnumMethod.setStatic(true);
        jsIsEnumMethod.setName("__isEnum");
        jsIsEnumMethod.getBody().add(new JSCodeSnippet("return false;"));
        addMethod(jsIsEnumMethod);
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
    
    public void addRequire(String name, String value, String postfix) {
        JSRequire jsRequire = new JSRequire() {
            @Override
            public Class<? extends Annotation> annotationType() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public String postfix() {
                if (postfix == null) return "";
                return postfix;
            }

            @Override
            public String value() {
                return value;
            }
        };

        this.requireSet.add(jsRequire);
    }

    void convertClassRequires() {
        for (CtTypeReference anImport : this.classRequires) {
            LOGGER.log(VERY_VERBOSE, String.format("Considering require '%s' in '%s'", anImport.getSimpleName(), this.getSimpleName()));

            if (anImport.getTypeDeclaration() == this.getCtClass()) {
                LOGGER.log(VERY_VERBOSE, "Omitting require for same class");
                continue;
            }

            if (anImport.isEnum() && new JJJOptionsHandler(anImport.getTypeDeclaration()).hasJJJ()) {
                LOGGER.log(VERY_VERBOSE, String.format("Generating require for annotated enum %s", anImport.getSimpleName()));
                JJJOptionsHandler jjjOptionsHandler = new JJJOptionsHandler(anImport);
                this.addRequire(jjjOptionsHandler.getName(), "./" + jjjOptionsHandler.getName(), "");
                continue;
            }

            CtTypeReference<JJJObject> jjjObjectRef = ctClass.getFactory().Type().createReference(JJJObject.class);
            boolean isSubtype = anImport.isSubtypeOf(jjjObjectRef);
            boolean hasAnno = new JJJOptionsHandler(anImport).hasJJJ();

            if (anImport.getTypeDeclaration() == null) {
                LOGGER.warn(" - unknown type required: " + anImport.getQualifiedName());
            } else if (!isSubtype && !hasAnno) {
                LOGGER.warn(" - non-transpiled type required: " + anImport.getQualifiedName());
            } else {
                LOGGER.log(VERY_VERBOSE, String.format("Generating require for %s", anImport.getQualifiedName()));
                JJJOptionsHandler jjjOptionsHandler = new JJJOptionsHandler(anImport);
                this.addRequire(jjjOptionsHandler.getName(), "./" + jjjOptionsHandler.getName(), "");
            }
        }
    }

    void extractAnnotatedRequires() {
        List<CtAnnotation<? extends Annotation>> annotations = ctClass.getAnnotations();
        for (CtAnnotation<?> ctAnnotation : annotations) {
            Annotation actualAnnotation = ctAnnotation.getActualAnnotation();
            if (actualAnnotation instanceof JSRequire) {
                this.requireSet.add((JSRequire) actualAnnotation);
            }
        }
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
            if (actualAnnotation instanceof JSPrequel) {
                printPrequel(builder, (JSPrequel) actualAnnotation);
            }
        }

        for (JSRequire jsRequire : this.requireSet){
            this.printRequire(builder, jsRequire);
        }
        
        builder.append(header.fullString(this.jjjOptions.getName()));
        builder.append(bodyString());

        for (JSClassBuilder nestedClassBuilder : this.nested) {
            builder.append(nestedClassBuilder.fullString());
            builder.append(this.jjjOptions.getName()).append(".").append(nestedClassBuilder.getSimpleName()).append(" = ").append(nestedClassBuilder.getSimpleName()).append(";");
        }

        appendStaticFields(builder);
        appendExport(builder);

        return builder.toString();
    }

    private void appendStaticFields(StringBuilder builder) {
        for (CtField<?> ctField : this.staticFields) {
            builder.append(this.getSimpleName()).append(".").append(ctField.getSimpleName());
            CtExpression<?> assignment = ctField.getAssignment();
            builder.append(" = ").append(assignment);
            builder.append(";\n");
        }
    }

    private void appendExport(StringBuilder builder) {
        builder.append("\nif (typeof module !== \"undefined\") module.exports = ");
        builder.append(this.getSimpleName());
        builder.append(";\n");
    }

    private void printRequire(StringBuilder builder, JSRequire jsRequire) {
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

    private void appendRequire(StringBuilder builder, CtTypeReference ref) {
        JJJOptionsHandler jjjOptionsHandler = new JJJOptionsHandler(ref);
        builder.append("const ");
        builder.append(jjjOptionsHandler.getName());
        builder.append(" = require(\"./");
        builder.append(jjjOptionsHandler.getName());
        builder.append("\")");
        builder.append(";\n");
    }

    private void printPrequel(StringBuilder builder, JSPrequel jsPrequel) {
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

    private void constructStaticFields() {
        Collection<CtFieldReference<?>> allFields = ctClass.getDeclaredFields();

        for (CtFieldReference<?> ctFieldRef : allFields) {
            if (!ctFieldRef.isStatic()) continue;

            CtField<?> ctField = ctFieldRef.getFieldDeclaration();

            if (ctField == null) {
                LOGGER.log(VERY_VERBOSE, "null static field initialization");
                continue;
            }

            if (ctField.getAnnotation(Transient.class) != null) {
                LOGGER.log(VERY_VERBOSE, "transient static field: " + ctField.getSimpleName());
                continue;
            }

            if (ctFieldRef.getDeclaration() == null) continue;
            this.staticFields.add(ctField);
        }
    }

    private void buildInitMethod() {
        JSElementList jsElementList = new JSElementList();

        /* add fields to js constructor */
        Collection<CtFieldReference<?>> allFields = ctClass.getDeclaredFields();

        for (CtFieldReference<?> ctFieldRef : allFields) {
            CtField<?> ctField = ctFieldRef.getFieldDeclaration();

            if (ctField == null) {
                LOGGER.log(VERY_VERBOSE, "field initializer is null: " + ctField.getSimpleName());
                continue;
            }

            if (ctField.getAnnotation(Transient.class) != null) {
                LOGGER.log(VERY_VERBOSE, "transient field: " + ctField.getSimpleName());
                continue;
            }

            if (ctFieldRef.getDeclaration() == null) {
                LOGGER.log(VERY_VERBOSE, "field declaration is null: " + ctField.getSimpleName());
                continue;
            }

            JSFieldDeclaration jsFieldDeclaration = new JSFieldDeclaration(ctFieldRef.getDeclaration());
            if (!jsFieldDeclaration.isStatic()) {
                LOGGER.log(Level.forName("VERBOSE", 450), "local field: " + ctField.getSimpleName());
                jsElementList.add(jsFieldDeclaration);
            } else {
                LOGGER.log(VERY_VERBOSE, "field is static: " + ctField.getSimpleName());
            }
        }

        JSMethodBuilder jsMethodBuilder = new JSMethodBuilder("__init");
        jsMethodBuilder.setBody(jsElementList);
        this.addMethod(jsMethodBuilder);
        this.classRequires.addAll(jsMethodBuilder.getRequires());
    }
}
