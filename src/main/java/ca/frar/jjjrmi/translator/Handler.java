package ca.frar.jjjrmi.translator;
import java.lang.reflect.InvocationTargetException;

/**
During encoding the object is provided by an outside source (user or field).  During decoding the object is provided
by instatiate and passed to decode.
@author edward
@param <T>
*/

public interface Handler <T>{
    public abstract T instatiate();
    public abstract void jjjDecode(RestoreHandler handler, T object) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException;
    public abstract void jjjEncode(EncodeHandler handler, T object) throws IllegalArgumentException, IllegalAccessException, EncoderException;
}
