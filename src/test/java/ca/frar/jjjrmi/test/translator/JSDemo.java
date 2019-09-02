import ca.frar.jjjrm.jsportal.JSExec;
import ca.frar.jjjrmi.test.testable.Parent;
import ca.frar.jjjrmi.translator.EncodedJSON;
import ca.frar.jjjrmi.translator.EncoderException;
import ca.frar.jjjrmi.translator.Translator;
import java.io.IOException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

public class JSDemo {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(JSDemo.class);    
    
    public static void main(String ... args) throws IllegalArgumentException, IllegalAccessException, EncoderException, IOException{
        JSExec jsExec = new JSExec();
        jsExec.start("src/test/js/main.js");
        String r = jsExec.stop();
        LOGGER.info(r);
    }    
}