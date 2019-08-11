package ca.frar.jjjrmi.jsbuilder;
import ca.frar.jjjrmi.utility.JJJOptionsHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtEnum;

/**
 * Will process all classes with the @JJJ annotation.
 *
 * @author edward
 */
@SuppressWarnings("unchecked")
public class JSParser extends AbstractProcessor<CtClass<?>> {
    final static Logger LOGGER = LogManager.getLogger(JSClassBuilder.class);
    private final JSClassContainer jsClassBuilders = new JSClassContainer();

    JSParser() {
        super();
    }

    @Override
    public void process(CtClass<?> ctClass) {
        JJJOptionsHandler jjjOptions = new JJJOptionsHandler(ctClass);
        
        if (!jjjOptions.generateJS()){
            LOGGER.info(" - " + ctClass.getSimpleName() + " has option generateJS=false");
            return;
        }        
        
        if (jjjOptions.isGenerated()) {
            LOGGER.info(" - " + ctClass.getSimpleName() + " has @Generated");
            return;
        }
        if (!jjjOptions.hasJJJ()) {
            LOGGER.info(" - " + ctClass.getSimpleName() + " no @JJJ");
            return;
        }
        if (ctClass.isEnum() && ctClass.getDeclaringType() != null){
            LOGGER.info(" - " + ctClass.getSimpleName() + " inner type");
            return;
        }
        
        LOGGER.info(" + " + ctClass.getSimpleName());
        if (ctClass.getDeclaringType() != null) LOGGER.info("declaring " + ctClass.getDeclaringType().getSimpleName());
        
        JSClassBuilder<?> jsClassBuilder;

        if (ctClass.isEnum()) {
            jsClassBuilder = new JSEnumBuilder<>((CtEnum<?>) ctClass).build();
            jsClassBuilders.addClass(jsClassBuilder);
        }        
        else {
            jsClassBuilder = new JSClassBuilder<>(ctClass).build();            
            jsClassBuilders.addClass(jsClassBuilder);
        }
    }

    public Iterable<JSClassBuilder<?>> jsClassBuilders() {
        return this.jsClassBuilders;
    }
}
