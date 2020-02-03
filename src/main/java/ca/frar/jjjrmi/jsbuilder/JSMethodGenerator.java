package ca.frar.jjjrmi.jsbuilder;

import ca.frar.jjjrmi.annotations.InvokeSuper;
import ca.frar.jjjrmi.annotations.JSParam;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.annotations.ServerSide;
import ca.frar.jjjrmi.jsbuilder.code.JSElementList;
import java.lang.annotation.Annotation;
import java.util.List;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.ModifierKind;

/**
 * Parse a ctMethod to fill in a JSMethodBuilder.
 *
 * @author Ed Armstrong
 * @param <T>
 */
public class JSMethodGenerator {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JJJRMI");
    private final JSMethodBuilder jsMethodBuilder;
    private final NativeJS nativeJSAnno;
    private final CtModifiable ctModifiable;
    private final CtExecutable ctExectuable;
    private final String name;

    public JSMethodGenerator(String name, CtModifiable ctModifiable, CtExecutable ctExectuable) {
        this.ctModifiable = ctModifiable;
        this.ctExectuable = ctExectuable;
        this.name = name;

        this.jsMethodBuilder = new JSMethodBuilder();
        nativeJSAnno = ctModifiable.getAnnotation(NativeJS.class);
    }

    public JSMethodBuilder run() {
        jsMethodBuilder.setName(this.name);

        if (nativeJSAnno != null) {
            if (!nativeJSAnno.value().isEmpty()) jsMethodBuilder.setName(nativeJSAnno.value());
            if (nativeJSAnno.isAsync()) jsMethodBuilder.setAsync(true);
            if (nativeJSAnno.isStatic()) jsMethodBuilder.setStatic(true);
            if (nativeJSAnno.isSetter()) jsMethodBuilder.setSetter(true);
            if (nativeJSAnno.isGetter()) jsMethodBuilder.setGetter(true);
        }

        if (ctModifiable.hasModifier(ModifierKind.STATIC)) jsMethodBuilder.setStatic(true);
        processArguments();

        if (ctModifiable.getAnnotation(ServerSide.class) != null) {
            jsMethodBuilder.setBody(String.format("return this.__jjjWebsocket.methodRequest(this, \"%s\", arguments);", this.name));
        } else {
            processBody();
        }

        if (ctModifiable.getAnnotation(InvokeSuper.class) != null) {
            jsMethodBuilder.setInvokeSuper(true);
        }

        List<CtAnnotation<? extends Annotation>> annotations = ctExectuable.getAnnotations();
        for (CtAnnotation ctAnnotation : annotations) {
            Annotation actualAnnotation = ctAnnotation.getActualAnnotation();
            if (actualAnnotation instanceof JSParam) {
                JSParam annotationParameter = (JSParam) actualAnnotation;
                JSParameter methodParameter = jsMethodBuilder.getParameter(annotationParameter.name());
                methodParameter.initializer = annotationParameter.init();
            }
        }

        return jsMethodBuilder;
    }

    private void processArguments() {
        List<CtParameter<?>> parameters = ctExectuable.getParameters();
        for (CtParameter<?> parameter : parameters) {
            jsMethodBuilder.addParameter(parameter.getSimpleName());
        }
    }

    private void processBody() {
        CtBlock<?> body = ctExectuable.getBody();
        JSElementList jsStatements = new JSElementList(body.getStatements());
        jsMethodBuilder.setBody(jsStatements);
    }
}
