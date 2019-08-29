package ca.frar.jjjrmi.translator;

import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author edward
 */
public interface RestoreHandler {
    public <T> T decodeField(String name) throws DecoderException;
}
