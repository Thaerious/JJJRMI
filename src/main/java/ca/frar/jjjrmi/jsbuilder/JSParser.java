package ca.frar.jjjrmi.jsbuilder;

import static ca.frar.jjjrmi.Global.VERY_VERBOSE;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.exceptions.JJJRMIWarning;
import ca.frar.jjjrmi.socket.HasWebsockets;
import ca.frar.jjjrmi.utility.JJJOptionsHandler;
import java.util.ArrayList;
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

    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JJJRMI");
    private final JSClassContainer jsClassBuilders = new JSClassContainer();
    private ArrayList<CtClass<?>> sourceClasses = new ArrayList<>();
    private String packageFileName = "packageFile";

    public JSParser() {
        super();
    }

    public void setPackageFileName(String packageFileName) {
        this.packageFileName = packageFileName;
    }

    public ArrayList<CtClass<?>> getSourceClasses() {
        return (ArrayList<CtClass<?>>) sourceClasses.clone();
    }

    @Override
    public void process(CtClass<?> ctClass) {
        if (test(ctClass)) {
            JSClassBuilder<?> jsClassBuilder = makeBuilder(ctClass).build();
            jsClassBuilders.addClass(jsClassBuilder);
        }
    }

    private JSClassBuilder<?> makeBuilder(CtClass<?> ctClass){
        if (ctClass.isEnum()){
            return new JSEnumBuilder<>((CtEnum<?>) ctClass);
        } 
        else if (new JJJOptionsHandler(ctClass).isSocket()){
            return new JSSocketBuilder(ctClass, packageFileName);
        }
        else {
            return new JSClassBuilder<>(ctClass); 
        }
    }
    
    public boolean test(CtClass<?> ctClass) {
        JJJOptionsHandler jjjOptions = new JJJOptionsHandler(ctClass);
        this.sourceClasses.add(ctClass);

        CtTypeReference<Object> jjjObjectType = ctClass.getFactory().Type().get(HasWebsockets.class).getReference();
        boolean isSubtype = ctClass.isSubtypeOf(jjjObjectType);

        /* enum class */
        if (ctClass.isEnum() && jjjOptions.hasJJJ()) {
            LOGGER.log(VERY_VERBOSE, "+------------------------------------------------------------------------------+");
            LOGGER.log(VERY_VERBOSE, "(+) Building javascript enumeration class: " + ctClass.getQualifiedName());
            return true;
        }

        /* socket class */
        if (jjjOptions.isSocket()) {
            LOGGER.log(VERY_VERBOSE, "+------------------------------------------------------------------------------+");
            LOGGER.log(VERY_VERBOSE, "(+) Building jjjrmi socket: " + ctClass.getQualifiedName());
            return true;
        }

        /* POJO class */
        if (!jjjOptions.generateJS()) {
            LOGGER.log(VERY_VERBOSE, "(-) " + ctClass.getQualifiedName() + " has option generateJS=false");
            return false;
        }
        
        if (ctClass.isEnum() && ctClass.getDeclaringType() != null) {
            LOGGER.log(VERY_VERBOSE, "(-) " + ctClass.getQualifiedName() + " inner type");
            return false;
        }
        
        if (jjjOptions.hasJJJ()) {
            LOGGER.log(VERY_VERBOSE, "+------------------------------------------------------------------------------+");
            LOGGER.log(Level.forName("VERBOSE", 475), "(+) " + ctClass.getQualifiedName() + "@JJJ");
            return true;
        }

        if (!isSubtype) {
            LOGGER.log(VERY_VERBOSE, "(-) " + ctClass.getQualifiedName() + " is not subtype of JJJObject");
            return false;
        }
        
        LOGGER.log(VERY_VERBOSE, "(-) " + ctClass.getQualifiedName() + " unknown reason");
        return false;
    }

    public Iterable<JSClassBuilder<?>> jsClassBuilders() {
        return this.jsClassBuilders;
    }
}
