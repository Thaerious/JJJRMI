package ca.frar.jjjrmi.translator;
import java.lang.reflect.InvocationTargetException;

public interface SelfHandler <T>{
    public abstract void decode(RestoreHandler handler) throws DecoderException;
    public abstract void encode(EncodeHandler handler) throws IllegalArgumentException, IllegalAccessException, EncoderException;
}
