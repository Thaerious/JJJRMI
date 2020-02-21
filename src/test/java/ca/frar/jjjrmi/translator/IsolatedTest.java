package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.testclasses.ArrayWrapper;
import ca.frar.jjjrmi.testclasses.Has;
import ca.frar.jjjrmi.testclasses.HasHandler;
import ca.frar.jjjrmi.testclasses.None;
import ca.frar.jjjrmi.testclasses.Simple;
import java.util.Arrays;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author edward
 */
public class IsolatedTest {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JJJRMI");
 
    public static void main(String ... args) throws JJJRMIException{
        new IsolatedTest().test_get_all_tracked_2();
    }
    
    public void test_get_all_tracked_2() throws JJJRMIException {      
    }  
    
}