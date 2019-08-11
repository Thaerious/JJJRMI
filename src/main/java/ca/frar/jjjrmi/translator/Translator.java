package ca.frar.jjjrmi.translator;

import ca.frar.jjjrmi.utility.BiMap;
import ca.frar.jjjrmi.handlers.ArrayListHandler;
import ca.frar.jjjrmi.handlers.HashMapHandler;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Consumer;
import org.json.JSONObject;

/**
Class for encoding and decoding RMI classes.
@author edward
 */
public final class Translator implements HasKeys, HasTranslateListeners <Object>{
    private HashMap<String, Handler> handlers = new HashMap<>();
    private ArrayList<Consumer<Object>> encodeListeners = new ArrayList<>();
    private ArrayList<Consumer<Object>> decodeListeners = new ArrayList<>();
    private ArrayList<Decoder> deferred = new ArrayList<>();
    private final BiMap<String, Object> objectMap = new BiMap<>();
    private final ArrayList<String> tempReferences = new ArrayList<>();
    private int nextKey = 0;

    public Translator() {
        this.setHandler(ArrayList.class, new ArrayListHandler());
        this.setHandler(HashMap.class, new HashMapHandler());
    }

    public Set<String> keySet(){
        return objectMap.keySet();
    }
    
    void deferDecoding(Decoder decoder) {
        this.deferred.add(decoder);
    }

    public boolean removeByKey(String key) {
        if (!objectMap.containsKey(key)) return false;
        this.objectMap.remove(key);
        return true;
    }

    public boolean removeByValue(Object obj) {
        if (!objectMap.containsValue(obj)) return false;
        this.objectMap.remove(objectMap.getKey(obj));
        return true;
    }

    public void setHandler(Class<?> aClass, Handler<?> handler) {
        this.handlers.put(aClass.getCanonicalName(), handler);
    }

    public boolean hasHandler(Class<?> aClass) {
        return this.handlers.containsKey(aClass.getCanonicalName());
    }

    Handler<?> getHandler(Class<?> aClass) {
        return this.handlers.get(aClass.getCanonicalName());
    }

    void addTempReference(String reference, Object object) {
        this.objectMap.put(reference, object);
        this.tempReferences.add(reference);
    }

    void clearTempReferences() {
        for (String ref : this.tempReferences) {
            this.removeByKey(ref);
        }
        this.tempReferences.clear();
    }

    void addReference(String reference, Object object) {
        this.objectMap.put(reference, object);
    }

    public boolean hasReference(String reference) {
        return objectMap.containsKey(reference);
    }

    public boolean hasReferredObject(Object object) {
        return objectMap.containsValue(object);
    }

    public Object getReferredObject(String reference) {
        return objectMap.get(reference);
    }

    public String getReference(Object object) {
        return objectMap.getKey(object);
    }

    public Collection<Object> getAllReferredObjects() {
        Collection<Object> values = this.objectMap.values();
        return new ArrayList<>(values);
    }

    /**
    Clear all memory of sent objects.  Use if the client of a websocket refreshes the browser before resending objects.
    Does not reset the nextKey variable in case the client hasn't cleared it's map.
    @return All objects previously in memory.
     */
    public final Object[] clear() {
        Object[] values = objectMap.values().toArray();
        objectMap.clear();
        tempReferences.clear();
        return values;
    }

    /**
    Translate a POJO to a JSON encoded object, storing the reference.  If this object has been previously sent an
    encoded reference is sent instead.
    @param object
    @return
     */
    public final EncodedJSON encode(Object object) throws IllegalArgumentException, IllegalAccessException, EncoderException {
        EncodedJSON toJSON = new Encoder(object, this).encode();
        this.clearTempReferences();
        return toJSON;
    }

    /**
    Translate a JSON encoded object to a POJO, returning the reference if it has previously been stored.
    @param json
    @return
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.NoSuchFieldException
     * @throws java.lang.NoSuchMethodException
     * @throws java.lang.reflect.InvocationTargetException
     */
    public final Object decode(JSONObject json) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        ObjectWrapper wrapper = new ObjectWrapper();

        new Decoder(json, this, null).decode(
            obj -> {
                while (!this.deferred.isEmpty()) this.deferred.remove(0).resume();
                this.clearTempReferences();
                wrapper.object = obj;
            }
        );

        return wrapper.object;
    }

    /**
    Translate a string into a JSON encoded object then to a POJO, returning the reference if it has previously
    been stored.
    @param json
    @return
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.NoSuchFieldException
     * @throws java.lang.NoSuchMethodException
     * @throws java.lang.reflect.InvocationTargetException
     */
    public final Object decode(String json) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        EncodedJSON jsonObject = new EncodedJSON(this, json);
        return this.decode(jsonObject);
    }

    /**
    Returns the next available key.
    @return
     */
    @Override
    public final synchronized String allocNextKey() {
        return "S" + (nextKey++);
    }

    public void addEncodeListener(Consumer<Object> lst) {
        this.encodeListeners.add(lst);
    }

    public void addDecodeListener(Consumer<Object> lst) {
        this.decodeListeners.add(lst);
    }

    @Override
    public void notifyEncode(Object object) {
        for (Consumer<Object> encodeListener : this.encodeListeners) encodeListener.accept(object);
    }

    @Override
    public void notifyDecode(Object object) {
        for (Consumer<Object> decodeListener : this.decodeListeners) decodeListener.accept(object);
    }
}