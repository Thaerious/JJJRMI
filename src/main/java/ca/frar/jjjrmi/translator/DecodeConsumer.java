package ca.frar.jjjrmi.translator;
import java.lang.reflect.InvocationTargetException;

/**
 * This class is the same as a Consumer but throws exceptions relevant to decoding.
 * It is invoked when an object is finished decoding and the calling object needs
 * to be notified of this, so it can perform the appropriate action.  This gets
 * deferred because some references can not be immediately decoded.
 * @author edward
 */
public interface DecodeConsumer {
    public void accept(Object object) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException;
}
