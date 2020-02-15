package ca.frar.jjjrmi.demo;

import ca.frar.jjjrmi.annotations.Handles;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.socket.JJJObject;
import ca.frar.jjjrmi.translator.HandlerFactory;
import ca.frar.jjjrmi.translator.Translator;
import ca.frar.jjjrmi.translator.encoder.AHandler;
import ca.frar.jjjrmi.translator.encoder.EncodedResult;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

public class Demo {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(Demo.class);    
    static Translator translator = new Translator();

    public static void main(String... args) throws JJJRMIException, IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Translator translator = new Translator();
        HandlerFactory instance = HandlerFactory.getInstance();
        List<Class<? extends AHandler<?>>> allHandlers = instance.getAllHandlers();
        for (Class<? extends AHandler<?>> hnd : allHandlers){
            Constructor<? extends AHandler<?>> constructor = hnd.getConstructor(EncodedResult.class);
            AHandler<?> newInstance = constructor.newInstance(new EncodedResult(translator));
            LOGGER.info(newInstance.getClass());
        }
    }
}
