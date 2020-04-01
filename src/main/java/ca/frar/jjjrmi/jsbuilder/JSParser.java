package ca.frar.jjjrmi.jsbuilder;

import ca.frar.jjjrmi.Global;
import static ca.frar.jjjrmi.Global.VERBOSE;
import static ca.frar.jjjrmi.Global.VERY_VERBOSE;
import ca.frar.jjjrmi.rmi.socket.JJJObject;
import ca.frar.jjjrmi.utility.JJJOptionsHandler;
import java.util.ArrayList;
import java.util.HashMap;
import static org.apache.logging.log4j.Level.DEBUG;
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
    ArrayList<String> skippedClasses = new ArrayList<>();
    HashMap<String, String> skipReason = new HashMap<>();
    
    public JSParser() {
        super();
    }

    public void setPackageFileName(String packageFileName) {
        this.packageFileName = packageFileName;
    }

    public ArrayList<CtClass<?>> getSourceClasses() {
        return (ArrayList<CtClass<?>>) sourceClasses.clone();
    }

    public ArrayList<String> getSkippedClasses(){
        return (ArrayList<String>) this.skippedClasses.clone();
    }
    
    public String getSkippedReason(String className){
        return this.skipReason.get(className);
    }    
    
    @Override
    public void process(CtClass<?> ctClass) {
        if (test(ctClass)) {
            LOGGER.log(VERBOSE, Global.header(ctClass.getQualifiedName()));
            JSClassBuilder<?> jsClassBuilder = makeBuilder(ctClass).build();
            jsClassBuilders.addClass(jsClassBuilder);
            LOGGER.log(VERBOSE, Global.tail());
        } else {
            skippedClasses.add(ctClass.getQualifiedName());            
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

        /* enum class */
        if (ctClass.isEnum() && jjjOptions.hasJJJ()) {
            LOGGER.log(DEBUG, "(+) Building javascript enumeration class: " + ctClass.getQualifiedName());
            return true;
        }

        /* socket class */
        if (jjjOptions.isSocket()) {
            LOGGER.log(DEBUG, "(+) Building jjjrmi socket: " + ctClass.getQualifiedName());
            return true;
        }

        /* POJO class */
        if (!jjjOptions.generateJS()) {
            skipReason.put(ctClass.getQualifiedName(), "has option generateJS=false");
            return false;
        }

        if (ctClass.isEnum() && ctClass.getDeclaringType() != null) {
            skipReason.put(ctClass.getQualifiedName(), " inner type");
            return false;
        }

        if (jjjOptions.hasJJJ()) {
            LOGGER.log(DEBUG, "(+) " + ctClass.getQualifiedName() + "@JJJ");
            return true;
        }

        if (!isSubtype) {
            skipReason.put(ctClass.getQualifiedName(), " is not subtype of JJJObject");
            return false;
        } else {
            LOGGER.log(DEBUG, "(+) " + ctClass.getQualifiedName() + " is subtype of JJJObject");
            return true;
        }
    }

    public Iterable<JSClassBuilder<?>> jsClassBuilders() {
        return this.jsClassBuilders;
    }
}
