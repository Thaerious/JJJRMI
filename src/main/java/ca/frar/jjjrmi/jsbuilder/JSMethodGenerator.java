package ca.frar.jjjrmi.jsbuilder;
import ca.frar.jjjrmi.annotations.InvokeSuper;
import ca.frar.jjjrmi.jsbuilder.code.JSElementList;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.annotations.ServerSide;
import java.util.List;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.ModifierKind;

public class JSMethodGenerator {
    private final CtMethod<?> ctMethod;
    private final JSClassBuilder<?> jsClassBuilder;
    private final JSMethodBuilder jsMethodBuilder;
    private final NativeJS nativeJSAnno;

    public JSMethodGenerator(CtMethod<?> ctMethod, JSClassBuilder<?> jsClassBuilder) {
        this.ctMethod = ctMethod;
        this.jsClassBuilder = jsClassBuilder;
        this.jsMethodBuilder = new JSMethodBuilder();
        nativeJSAnno = ctMethod.getAnnotation(NativeJS.class);
    }

    /**
    @return true if processing took place.
     */
    public void run() {
        jsMethodBuilder.setName(ctMethod.getSimpleName());
        
        if (nativeJSAnno != null) {
            if (!nativeJSAnno.value().isEmpty()) jsMethodBuilder.setName(nativeJSAnno.value());
            if (nativeJSAnno.isAsync()) jsMethodBuilder.setAsync(true);
            if (nativeJSAnno.isStatic()) jsMethodBuilder.setStatic(true);
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
        
        jsClassBuilder.addMethod(jsMethodBuilder);
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
        jsMethodBuilder.setBody(jsStatements.unscoped());
    }
}
