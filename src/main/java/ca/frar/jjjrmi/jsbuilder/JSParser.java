package ca.frar.jjjrmi.jsbuilder;

import static ca.frar.jjjrmi.Global.VERBOSE;
import static ca.frar.jjjrmi.Global.VERY_VERBOSE;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.socket.JJJObject;
import ca.frar.jjjrmi.utility.JJJOptionsHandler;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
            LOGGER.log(VERBOSE, "----- [" + ctClass.getQualifiedName() + "] -----");
            JSClassBuilder<?> jsClassBuilder = makeBuilder(ctClass).build();
            jsClassBuilders.addClass(jsClassBuilder);
            LOGGER.log(VERBOSE, "-----");
        } else {
            LOGGER.log(VERBOSE, "Skipping " + ctClass.getQualifiedName());
        }
    }

    private JSClassBuilder<?> makeBuilder(CtClass<?> ctClass) {
        LOGGER.debug(new JJJOptionsHandler(ctClass).isHandler());

        
        
        if (ctClass.isEnum()) {
            return new JSEnumBuilder<>((CtEnum<?>) ctClass);
        } else if (new JJJOptionsHandler(ctClass).isHandler()) {
            return new JSHandlerBuilder<>(ctClass);
        } else if (new JJJOptionsHandler(ctClass).isSocket()) {
            return new JSSocketBuilder(ctClass, packageFileName);
        } else {
            return new JSClassBuilder<>(ctClass);
        }
    }

    public boolean test(CtClass ctClass) {
        JJJOptionsHandler jjjOptions = new JJJOptionsHandler(ctClass);
        this.sourceClasses.add(ctClass);

        CtTypeReference reference = ctClass.getReference();
        CtTypeReference<JJJObject> jjjObjectRef = ctClass.getFactory().Type().createReference(JJJObject.class);
        boolean isSubtype = reference.isSubtypeOf(jjjObjectRef);

        LOGGER.log(VERY_VERBOSE, "+------------------------------------------------------------------------------+");
        LOGGER.log(VERY_VERBOSE, "(?) Considering class: " + ctClass.getQualifiedName());

        /* enum class */
        if (ctClass.isEnum() && jjjOptions.hasJJJ()) {
            LOGGER.log(VERY_VERBOSE, "(+) Building javascript enumeration class: " + ctClass.getQualifiedName());
            return true;
        }

        /* socket class */
        if (jjjOptions.isSocket()) {
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
            LOGGER.log(VERY_VERBOSE, "(+) " + ctClass.getQualifiedName() + "@JJJ");
            return true;
        }

        if (!isSubtype) {
            LOGGER.log(VERY_VERBOSE, "(-) " + ctClass.getQualifiedName() + " is not subtype of JJJObject");
            return false;
        } else {
            LOGGER.log(VERY_VERBOSE, "(+) " + ctClass.getQualifiedName() + " is subtype of JJJObject");
            return true;
        }
    }

    public Iterable<JSClassBuilder<?>> jsClassBuilders() {
        return this.jsClassBuilders;
    }
}
