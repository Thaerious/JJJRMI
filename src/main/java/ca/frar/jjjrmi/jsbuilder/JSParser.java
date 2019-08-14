package ca.frar.jjjrmi.jsbuilder;
import ca.frar.jjjrmi.socket.JJJObject;
import ca.frar.jjjrmi.translator.HasWebsockets;
import ca.frar.jjjrmi.utility.JJJOptionsHandler;
import org.apache.logging.log4j.Level;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.reference.CtTypeReference;

/**
 * Will process all classes that implement the HasWebsockets interface.
 *
 * @author edward
 */
@SuppressWarnings("unchecked")
public class JSParser extends AbstractProcessor<CtClass<?>> {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(JSParser.class);
    private final JSClassContainer jsClassBuilders = new JSClassContainer();

    public JSParser() {
        super();
    }

    @Override
    public void process(CtClass<?> ctClass) {
        JJJOptionsHandler jjjOptions = new JJJOptionsHandler(ctClass);
                        
        CtTypeReference<Object> jjjObjectType = ctClass.getFactory().Type().get(HasWebsockets.class).getReference();        
        boolean isSubtype = ctClass.isSubtypeOf(jjjObjectType);
        
        /* enum class */
        if (ctClass.isEnum() && jjjOptions.hasJJJ()) {
            LOGGER.log(Level.forName("VERY-VERBOSE", 475), "(+) Building javascript enumeration class: " + ctClass.getQualifiedName());
            JSClassBuilder<?> jsClassBuilder = new JSEnumBuilder<>((CtEnum<?>) ctClass).build();
            jsClassBuilders.addClass(jsClassBuilder);
            return;
        }    
        
        /* POJO class */
        if (!isSubtype){
            LOGGER.log(Level.forName("VERY-VERBOSE", 475), "(-)" + ctClass.getQualifiedName() + " is not subtype of JJJObject");
            return;
        }
        if (!jjjOptions.generateJS()){
            LOGGER.log(Level.forName("VERY-VERBOSE", 475), "(-)" + ctClass.getQualifiedName() + " has option generateJS=false");
            return;
        }               
        if (jjjOptions.isGenerated()) {
            LOGGER.log(Level.forName("VERY-VERBOSE", 475), "(-)" + ctClass.getQualifiedName() + " has @Generated");
            return;
        }
        if (ctClass.isEnum() && ctClass.getDeclaringType() != null){
            LOGGER.log(Level.forName("VERY-VERBOSE", 475), "(-)" + ctClass.getQualifiedName() + " inner type");
            return;
        }        
        if (!jjjOptions.hasJJJ()) {
            LOGGER.log(Level.forName("VERY-VERBOSE", 475), "(+)" + ctClass.getQualifiedName() + " no @JJJ, using defaults");
        }
        
        LOGGER.log(Level.forName("VERBOSE", 475), "(+)" + ctClass.getQualifiedName());
        if (ctClass.getDeclaringType() != null) LOGGER.log(Level.forName("VERBOSE", 450), "declaring " + ctClass.getDeclaringType().getSimpleName());
        
        JSClassBuilder<?> jsClassBuilder;

        LOGGER.log(Level.forName("VERY-VERBOSE", 475), "Building javascript object class");
        jsClassBuilder = new JSClassBuilder<>(ctClass).build();            
        jsClassBuilders.addClass(jsClassBuilder);
    }

    public Iterable<JSClassBuilder<?>> jsClassBuilders() {
        return this.jsClassBuilders;
    }
}
