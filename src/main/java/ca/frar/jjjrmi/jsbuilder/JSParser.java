package ca.frar.jjjrmi.jsbuilder;
import ca.frar.jjjrmi.translator.HasWebsockets;
import ca.frar.jjjrmi.utility.JJJOptionsHandler;
import java.util.ArrayList;
import org.apache.logging.log4j.Level;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.reference.CtTypeReference;

/**
 * Will process all classes that implement the HasWebsockets interface.
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
    
    public void setPackageFileName(String packageFileName){
        this.packageFileName = packageFileName;
    }

    public ArrayList<CtClass<?>> getSourceClasses(){
        return (ArrayList<CtClass<?>>) sourceClasses.clone();
    }
    
    @Override
    public void process(CtClass<?> ctClass) {
        LOGGER.trace("JSParser.process(" + ctClass.getSimpleName() + ")");
        JJJOptionsHandler jjjOptions = new JJJOptionsHandler(ctClass);
        this.sourceClasses.add(ctClass);
                        
        CtTypeReference<Object> jjjObjectType = ctClass.getFactory().Type().get(HasWebsockets.class).getReference();        
        boolean isSubtype = ctClass.isSubtypeOf(jjjObjectType);
        
        /* enum class */
        if (ctClass.isEnum() && jjjOptions.hasJJJ()) {
            LOGGER.log(Level.forName("VERY-VERBOSE", 475), "+------------------------------------------------------------------------------+");
            LOGGER.log(Level.forName("VERY-VERBOSE", 475), "(+) Building javascript enumeration class: " + ctClass.getQualifiedName());
            JSClassBuilder<?> jsClassBuilder = new JSEnumBuilder<>((CtEnum<?>) ctClass).build();
            jsClassBuilders.addClass(jsClassBuilder);
            return;
        }    
        
        /* socket class */
        if (jjjOptions.isSocket()){
            LOGGER.log(Level.forName("VERY-VERBOSE", 475), "+------------------------------------------------------------------------------+");
            LOGGER.log(Level.forName("VERY-VERBOSE", 475), "(+) Building jjjrmi socket: " + ctClass.getQualifiedName());  
            JSClassBuilder jsSocketBuilder = new JSSocketBuilder(ctClass, packageFileName).build();
            jsClassBuilders.addClass(jsSocketBuilder);
        }
        
        /* POJO class */
        if (!isSubtype){
            LOGGER.log(Level.forName("VERY-VERBOSE", 475), "(-) " + ctClass.getQualifiedName() + " is not subtype of JJJObject");
            return;
        }
        if (!jjjOptions.generateJS()){
            LOGGER.log(Level.forName("VERY-VERBOSE", 475), "(-) " + ctClass.getQualifiedName() + " has option generateJS=false");
            return;
        }               
        if (jjjOptions.isGenerated()) {
            LOGGER.log(Level.forName("VERY-VERBOSE", 475), "(-) " + ctClass.getQualifiedName() + " has @Generated");
            return;
        }
        if (ctClass.isEnum() && ctClass.getDeclaringType() != null){
            LOGGER.log(Level.forName("VERY-VERBOSE", 475), "(-) " + ctClass.getQualifiedName() + " inner type");
            return;
        }        
        if (!jjjOptions.hasJJJ()) {
            LOGGER.log(Level.forName("VERY-VERBOSE", 475), "+------------------------------------------------------------------------------+");
            LOGGER.log(Level.forName("VERBOSE", 475), "(+) " + ctClass.getQualifiedName());
        } else {
            LOGGER.log(Level.forName("VERY-VERBOSE", 475), "+------------------------------------------------------------------------------+");
            LOGGER.log(Level.forName("VERBOSE", 475), "(+) " + ctClass.getQualifiedName() + "@JJJ");
        }
                
        if (ctClass.getDeclaringType() != null) LOGGER.log(Level.forName("VERBOSE", 450), "declaring " + ctClass.getDeclaringType().getSimpleName());
        
        try{
            JSClassBuilder<?> jsClassBuilder = new JSClassBuilder<>(ctClass).build(); 
            LOGGER.log(Level.forName("VERY-VERBOSE", 475), "+------------------------------------------------------------------------------+");
            jsClassBuilders.addClass(jsClassBuilder);
        } catch (Exception ex){
            throw new JSClassBuildException(ex, ctClass);
        }
    }

    public Iterable<JSClassBuilder<?>> jsClassBuilders() {
        return this.jsClassBuilders;
    }
}
