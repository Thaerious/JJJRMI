package ca.frar.jjjrmi.demo;

import ca.frar.jjjrmi.annotations.Handles;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.socket.JJJObject;
import ca.frar.jjjrmi.translator.Translator;
import ca.frar.jjjrmi.translator.encoder.AHandler;
import java.io.IOException;
import java.util.Set;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

public class Demo {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(Demo.class);    
    static Translator translator = new Translator();

    public static void main(String... args) throws JJJRMIException, IOException {
        SubTypesScanner subTypesScanner = new SubTypesScanner(false);  
        TypeAnnotationsScanner typeAnnotationsScanner = new TypeAnnotationsScanner();
        Reflections reflections = new Reflections("", subTypesScanner, typeAnnotationsScanner);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Handles.class);        

        for (Class<?> aClass : classes) {
            LOGGER.debug(aClass);
        }
        LOGGER.debug("number of classes: " + classes.size());
    }
}
