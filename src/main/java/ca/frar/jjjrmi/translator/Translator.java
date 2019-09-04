package ca.frar.jjjrmi.translator;

import ca.frar.jjjrmi.annotations.Handles;
import ca.frar.jjjrmi.utility.BiMap;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Consumer;
import org.json.JSONObject;

/**
 * Class for encoding and decoding RMI classes. Classes added to the encoder
 * will be tracked. These objects will be assigned a reference string which can
 * be used to retrieve the object. All reference strings assigned by the
 * Translator will start with the 'S' character (stands for Sever side).<br>
 * A Translator does not have to work on a JJJObject.  To control the behaviour
 * of the translator on an object use the @JJJ annotation.
 *
 * @author edward
 */
public final class Translator implements HasKeys {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(Translator.class);    
    private HashMap<String, IHandler> handlers = new HashMap<>();
    private ArrayList<Consumer<Object>> encodeListeners = new ArrayList<>();
    private ArrayList<Consumer<Object>> decodeListeners = new ArrayList<>();
    private ArrayList<Decoder> deferred = new ArrayList<>();
    private final BiMap<String, Object> objectMap = new BiMap<>();
    private final ArrayList<String> tempReferences = new ArrayList<>();
    private int nextKey = 0;

    /**
     * Defer action on the decoder. This is used when decoding a reference for
     * which the object has not yet been encountered.
     *
     * @param decoder
     */
    void deferDecoding(Decoder decoder) {
        this.deferred.add(decoder);
    }

    public boolean removeByValue(Object obj) {
        if (!objectMap.containsValue(obj)) {
            return false;
        }
        this.objectMap.remove(objectMap.getKey(obj));
        return true;
    }

    public void seekHandlers() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        ClassGraph classGraph = new ClassGraph();
        classGraph.enableAllInfo();
        ScanResult scanResult = classGraph.scan();
        
        ClassInfoList allClasses = scanResult.getClassesWithAnnotation(Handles.class.getCanonicalName());

        for (ClassInfo ci : allClasses){
            AnnotationInfo annotationInfo = ci.getAnnotationInfo(Handles.class.getCanonicalName());
            String value = annotationInfo.getParameterValues().get("value").getValue().toString();

            Class<?> handler = ClassLoader.getSystemClassLoader().loadClass(ci.getName());
            Class<?> handles = ClassLoader.getSystemClassLoader().loadClass(value);
            
            Constructor<?> constructor = handler.getConstructor();            
            this.setHandler(handles, (IHandler) constructor.newInstance());
        }
    }
    
    public void setHandler(Class<?> aClass, IHandler handler) {
        this.handlers.put(aClass.getCanonicalName(), handler);
    }

    public boolean hasHandler(Class<?> aClass) {
        return this.handlers.containsKey(aClass.getCanonicalName());
    }

    IHandler<?> getHandler(Class<?> aClass) {
        return this.handlers.get(aClass.getCanonicalName());
    }

    /**
     * Add a reference that will be removed when this round of
     * encoding/decoding.<br>
     * See: {@link ca.frar.jjjrmi.annotations.JJJ#retain @JJJ.retain()}
     *
     * is complete.
     *
     * @param reference
     * @param object
     */
    void addTempReference(String reference, Object object) {
        this.objectMap.put(reference, object);
        this.tempReferences.add(reference);
    }

    /**
     * Remove all temporary references from this translator. They will get
     * re-encoded when next encountered.
     */
    void clearTempReferences() {
        for (String ref : this.tempReferences) {
            this.objectMap.remove(ref);
        }
        this.tempReferences.clear();
    }

    /**
     * Add a reference to the this translator.
     *
     * @param reference
     * @param object
     */
    void addReference(String reference, Object object) {
        this.objectMap.put(reference, object);
    }

    /**
     * Determine if the JSON Encoded Reference is valid.
     *
     * @param reference
     * @return
     */
    public boolean hasReference(String reference) {
        return objectMap.containsKey(reference);
    }

    /**
     * Determine if an object has been encoded (and saved) by this translator.
     *
     * @param object
     * @return
     */
    public boolean hasReferredObject(Object object) {
        return objectMap.containsValue(object);
    }

    /**
     * Retrieve an object from a JSON encoded reference.
     *
     * @param reference
     * @return
     */
    public Object getReferredObject(String reference) {
        return objectMap.get(reference);
    }

    /**
     * Return a JSON encoded reference to the given object. The reference is
     * only valid for this translator.
     *
     * @param object
     * @return A JSON encoded reference
     */
    public String getReference(Object object) {
        return objectMap.getKey(object);
    }

    /**
     * Return a collection of all objects tracked by this Translator.
     *
     * @return
     */
    public Collection<Object> getAllReferredObjects() {
        Collection<Object> values = this.objectMap.values();
        return new ArrayList<>(values);
    }

    /**
     * Clear all memory of sent objects. Use if the client of a websocket
     * refreshes the browser before resending objects. Does not reset the
     * nextKey variable in case the client hasn't cleared it's map.
     *
     * @return All objects previously in memory.
     */
    public final Object[] clear() {
        Object[] values = objectMap.values().toArray();
        objectMap.clear();
        tempReferences.clear();
        return values;
    }

    /**
     * Translate a POJO to a JSON encoded object, storing the reference. If this
     * object has been previously sent an encoded reference is sent instead.
     *
     * @param object
     * @return
     */
    public final EncodedJSON encode(Object object) throws IllegalArgumentException, IllegalAccessException, EncoderException {
        LOGGER.trace("Translator.encode(" + object.getClass().getSimpleName() + ")");
        EncodedJSON toJSON = new Encoder(object, this).encode();
        this.clearTempReferences();
        return toJSON;
    }

    /**
     * Translate a JSON encoded object to a POJO, returning the reference if it
     * has previously been stored.
     *
     * @param json
     * @return
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.NoSuchFieldException
     * @throws java.lang.NoSuchMethodException
     * @throws java.lang.reflect.InvocationTargetException
     */
    final Object decode(JSONObject json) throws DecoderException {
            ObjectWrapper wrapper = new ObjectWrapper();
            
            new Decoder(json, this, null).decode(
                    obj -> {
                        while (!this.deferred.isEmpty()) {
                            this.deferred.remove(0).resume();
                        }
                        this.clearTempReferences();
                        wrapper.object = obj;
                    }
            );
            return wrapper.object;
    }

    /**
     * Translate a string into a JSON encoded object then to a POJO, returning
     * the reference if it has previously been stored.
     *
     * @param json
     * @return
     * @throws ca.frar.jjjrmi.translator.DecoderException
     */
    public final Object decode(String json) throws DecoderException {
        EncodedJSON jsonObject = new EncodedJSON(this, json);
        return this.decode(jsonObject);
    }

    /**
     * Returns the next available key.
     *
     * @return
     */
    @Override
    public final synchronized String allocNextKey() {
        return "S" + (nextKey++);
    }
}
