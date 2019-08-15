package ca.frar.jjjrmi.jsbuilder;

import ca.frar.jjjrmi.jsbuilder.code.JSElementList;
import ca.frar.jjjrmi.jsbuilder.code.JSFieldDeclaration;
import ca.frar.jjjrmi.jsbuilder.code.JSSuperConstructor;
import java.util.Collection;
import java.util.List;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtFieldReference;
import spoon.support.reflect.code.CtInvocationImpl;
import spoon.support.reflect.declaration.CtConstructorImpl;

public class JSConstructorGenerator {

    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(JSConstructorGenerator.class);
    private final CtClass<?> ctClass;
    private final CtExecutable<?> ctConstructor;
    private final JSClassBuilder<?> jsClassBuilder;
    private final JSMethodBuilder jsMethodBuilder;

    public JSConstructorGenerator(CtClass<?> ctClass, JSClassBuilder<?> jsClassBuilder) {
        this.ctClass = ctClass;
        this.ctConstructor = null;
        this.jsClassBuilder = jsClassBuilder;
        this.jsMethodBuilder = new JSMethodBuilder();
        jsMethodBuilder.setName("constructor");
    }

    public JSConstructorGenerator(CtClass<?> ctClass, CtConstructor<?> ctConstructor, JSClassBuilder<?> jsClassBuilder) {
        this.ctClass = ctClass;
        this.ctConstructor = ctConstructor;
        this.jsClassBuilder = jsClassBuilder;
        this.jsMethodBuilder = new JSMethodBuilder();
        jsMethodBuilder.setName("constructor");
    }

    public void run() {
        if (ctConstructor != null) {
            processArguments();
            processBody();
        } else {
            processEmptyBody();
        }

        jsClassBuilder.addMethod(jsMethodBuilder);
    }

    private void processArguments() {
        List<CtParameter<?>> parameters = ctConstructor.getParameters();
        if (!parameters.isEmpty()) {
            LOGGER.warn("Constructor for class '" + ctClass.getQualifiedName() + "' has a non-empty parameter list.");
        }

        for (CtParameter<?> parameter : parameters) {
            jsMethodBuilder.addParameter(parameter.getSimpleName());
        }
    }

    private JSElementList constructFields() {
        JSElementList jsElementList = new JSElementList();
        
        /* add fields to js constructor */
        Collection<CtFieldReference<?>> allFields = ctClass.getAllFields();
        for (CtFieldReference<?> ctField : allFields) {
            if (ctField.getDeclaration() == null) {
                continue;
            }
            JSFieldDeclaration jsFieldDeclaration = new JSFieldDeclaration(ctField.getDeclaration());
            if (jsFieldDeclaration.hasAssignment()) {
                if (jsFieldDeclaration.isStatic()) {
                    jsClassBuilder.addStaticField(jsFieldDeclaration);
                } else {
                    jsElementList.add(jsFieldDeclaration);
                }
            }
        }
        
        return jsElementList;
    }

    private void processEmptyBody() {
        JSElementList jsElementList = this.constructFields();
        boolean hasExtend = this.jsClassBuilder.getHeader().hasExtend();
        if (hasExtend) jsElementList.add(0, new JSSuperConstructor());
        jsMethodBuilder.setBody(jsElementList.unscoped());
    }

    private void processBody() {
        CtBlock<?> body;
        if (this.ctConstructor == null) {
            body = this.ctClass.getFactory().createBlock();
        } else {
            body = ctConstructor.getBody();
        }

        List<CtStatement> ctStatements = body.getStatements();
        JSElementList jsElementList = this.constructFields();
        
        CtInvocationImpl<?> superInvoke = null;

        for (CtStatement statement : ctStatements) {
            if (statement.getClass() == CtInvocationImpl.class) {
                CtInvocationImpl<?> invocation = (CtInvocationImpl) statement;
                if (invocation.getTarget() == null) {
                    superInvoke = invocation;
                    continue;
                }
            }

            jsElementList.addCtCodeElement(statement);
        }

        if (superInvoke != null && this.jsClassBuilder.getHeader().hasExtend()) {
            jsElementList.add(0, new JSSuperConstructor(superInvoke));
        }

        jsMethodBuilder.setBody(jsElementList.unscoped());
    }
}
