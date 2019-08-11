package ca.frar.jjjrmi.socket;
import ca.frar.jjjrmi.annotations.ServerSide;
import java.lang.reflect.Method;
import java.util.HashMap;

public class MethodBank {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(MethodBank.class);
    private class MethodStore extends HashMap<String, Method> {};
    private final HashMap<Class<?>, MethodStore> classStore = new HashMap<>();

    public Method getMethod(Class<?> aClass, String methodName) throws NoSuchMethodException {
        if (!classStore.containsKey(aClass)) storeClass(aClass);
        MethodStore methodStore = classStore.get(aClass);
        if (!methodStore.containsKey(methodName)){
            throw new NoSuchMethodException("Method " + aClass.getSimpleName() + "." + methodName + " not found in methodBank.");
        }
        return methodStore.get(methodName);
    }

    private void storeClass(Class<?> aClass) {
        LOGGER.debug("Retrieving methods for class: " + aClass.getSimpleName());
        MethodStore methodStore = new MethodStore();

        Class<?> current = aClass;
        while (current != Object.class) {
            for (Method method : current.getMethods()) {                
                if (method.getAnnotation(ServerSide.class) == null){
                    LOGGER.debug("no ServerSide annotation: " + current.getSimpleName() + "." + method.getName());
                    continue;
                }
                if (methodStore.containsKey(method.getName())){
                    LOGGER.debug("duplicate method: " + current.getSimpleName() + "." + method.getName());
                    continue;
                }
                
                LOGGER.debug("method retrieved: " + current.getSimpleName() + "." + method.getName());
                methodStore.put(method.getName(), method);
            }
            current = current.getSuperclass();
        }
        classStore.put(aClass, methodStore);
    }
}