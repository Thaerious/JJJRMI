package ca.frar.jjjrmi.translator;

public interface HasTranslateListeners <T> {
    public void notifyEncode(T object);
    public void notifyDecode(T object);
}
