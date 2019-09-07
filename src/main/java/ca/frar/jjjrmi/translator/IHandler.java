package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.EncoderException;

/**
During encoding the object is provided by an outside source (user or field).  During decoding the object is provided
by instatiate and passed to decode.
@author edward
@param <T>
*/

public interface IHandler <T>{
    public abstract T instatiate();
    public abstract void jjjDecode(RestoreHandler handler, T object) throws DecoderException;
    public abstract void jjjEncode(EncodeHandler handler, T object) throws EncoderException;
}
