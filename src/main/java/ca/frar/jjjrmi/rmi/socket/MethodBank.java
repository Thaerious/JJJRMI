package ca.frar.jjjrmi.rmi.socket;
import ca.frar.jjjrmi.annotations.ServerSide;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * A JIT collection of methods.  A method is searched for the first time it is
 * encountered then it is saved for future requests.
 * @author Ed Armstrong
 */
public class MethodBank {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JJJRMI");
    private class MethodStore extends HashMap<String, Method> {};
    private final HashMap<Class<?>, MethodStore> classStore = new HashMap<>();

    public Method getMethod(Class<?> aClass, String methodName) throws NoSuchMethodException {
        if (!classStore.containsKey(aClass)) storeClass(aClass);
        MethodStore methodStore = classStore.get(aClass);
        if (!methodStore.containsKey(methodName)){
            String msg = String.format("Method %s not found in class %s. Ensure both class and method are public.", methodName, aClass.getSimpleName());
            throw new NoSuchMethodException(msg);
        }
        return methodStore.get(methodName);
    }

    private void storeClass(Class<?> aClass) {
        LOGGER.trace("storeClass: " + aClass.getSimpleName());
        MethodStore methodStore = new MethodStore();

        Class<?> current = aClass;
        while (current != Object.class) {
            for (Method method : current.getMethods()) {                
                if (method.getAnnotation(ServerSide.class) == null){
                    continue;
                }
                if (methodStore.containsKey(method.getName())){
                    continue;
                }
                
                methodStore.put(method.getName(), method);
            }
            current = current.getSuperclass();
        }
        classStore.put(aClass, methodStore);
    }
}