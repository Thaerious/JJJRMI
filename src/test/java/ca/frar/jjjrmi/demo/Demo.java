package ca.frar.jjjrmi.demo;

import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.translator.Translator;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Demo {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(Demo.class);    
    static Translator translator = new Translator();

    public static void main(String... args) throws JJJRMIException, IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//        Translator translator = new Translator();
//        HandlerFactory instance = HandlerFactory.getInstance();
//        List<Class<? extends AHandler<?>>> allHandlers = instance.getAllHandlers();
//        for (Class<? extends AHandler<?>> hnd : allHandlers){
//            Constructor<? extends AHandler<?>> constructor = hnd.getConstructor(TranslatorResult.class);
//            AHandler<?> newInstance = constructor.newInstance(new TranslatorResult(translator));
//            LOGGER.info(newInstance.getClass());
//        }
    }
}
