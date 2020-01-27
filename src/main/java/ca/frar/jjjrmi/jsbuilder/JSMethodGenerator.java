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
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.ModifierKind;

/**
 * Parse a ctMethod to fill in a JSMethodBuilder.
 * @author Ed Armstrong
 */
public class JSMethodGenerator {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JJJRMI");
    private final CtMethod<?> ctMethod;
    private final JSMethodBuilder jsMethodBuilder;
    private final NativeJS nativeJSAnno;

    public JSMethodGenerator(CtMethod<?> ctMethod) {
        this.ctMethod = ctMethod;
        this.jsMethodBuilder = new JSMethodBuilder();
        nativeJSAnno = ctMethod.getAnnotation(NativeJS.class);
    }
    
    public JSMethodBuilder run() {
        jsMethodBuilder.setName(ctMethod.getSimpleName());

        if (nativeJSAnno != null) {
            if (!nativeJSAnno.value().isEmpty()) jsMethodBuilder.setName(nativeJSAnno.value());
            if (nativeJSAnno.isAsync()) jsMethodBuilder.setAsync(true);
            if (nativeJSAnno.isStatic()) jsMethodBuilder.setStatic(true);
            if (nativeJSAnno.isSetter()) jsMethodBuilder.setSetter(true);
            if (nativeJSAnno.isGetter()) jsMethodBuilder.setGetter(true);
        }

        if (ctMethod.hasModifier(ModifierKind.STATIC)) jsMethodBuilder.setStatic(true);
        processArguments();

        if (ctMethod.getAnnotation(ServerSide.class) != null) {
            jsMethodBuilder.setBody(String.format("return this.__jjjWebsocket.methodRequest(this, \"%s\", arguments);", ctMethod.getSimpleName()));
        } else {
            processBody();
        }

        if (ctMethod.getAnnotation(InvokeSuper.class) != null) {
            jsMethodBuilder.setInvokeSuper(true);
        }

        List<CtAnnotation<? extends Annotation>> annotations = ctMethod.getAnnotations();
        for (CtAnnotation ctAnnotation : annotations) {
            Annotation actualAnnotation = ctAnnotation.getActualAnnotation();
            if (actualAnnotation instanceof JSParam){
                JSParam annotationParameter = (JSParam) actualAnnotation;
                JSParameter methodParameter = jsMethodBuilder.getParameter(annotationParameter.name());
                methodParameter.initializer = annotationParameter.init();
            }
        }
        
        return jsMethodBuilder;
    }

    private void processArguments() {
        List<CtParameter<?>> parameters = ctMethod.getParameters();
        for (CtParameter<?> parameter : parameters) {
            jsMethodBuilder.addParameter(parameter.getSimpleName());
        }
    }

    private void processBody() {
        CtBlock<?> body = ctMethod.getBody();
        JSElementList jsStatements = new JSElementList(body.getStatements());
        jsMethodBuilder.setBody(jsStatements);
    }
}
