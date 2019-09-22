package ca.frar.jjjrmi.demo;

import ca.frar.jjjrm.test.jsportal.JSExec;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import java.io.IOException;

public class JSDemo {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JJJRMI");    
    
    public static void main(String ... args) throws JJJRMIException, IOException{
        JSExec jsExec = new JSExec();
        jsExec.start("src/test/js/main.js");
        String r = jsExec.stop();
        LOGGER.info(r);
    }    
}