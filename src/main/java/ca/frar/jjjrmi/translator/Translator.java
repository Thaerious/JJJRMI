package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.exceptions.UntrackedObjectException;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.exceptions.UnknownReferenceException;
import ca.frar.jjjrmi.exceptions.RootException;
import ca.frar.jjjrmi.exceptions.TranslatorException;
import ca.frar.jjjrmi.utility.BiMap;
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
 * A Translator does not have to work on a JJJObject. To control the behaviour
 * of the translator on an object use the @JJJ annotation.
 *
 * @author edward
 */
@JJJ(insertJJJMethods = false)
public final class Translator {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JJJRMI");
    private HashMap<String, Class<? extends AHandler<?>>> handlers = new HashMap<>();
    private ArrayList<Consumer<Object>> encodeListeners = new ArrayList<>();
    private ArrayList<Consumer<Object>> decodeListeners = new ArrayList<>();
    private final BiMap<String, Object> objectMap = new BiMap<>();
    private final ArrayList<String> tempReferences = new ArrayList<>();
    private int nextKey = 0;
    private static String referencePrequel = "S";

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
    @NativeJS
    void addTempReference(String reference, Object object) {
        this.objectMap.put(reference, object);
        this.tempReferences.add(reference);
    }

    /**
     * Remove all temporary references from this translator. They will get
     * re-encoded when next encountered.
     */
    @NativeJS
    void clearTempReferences() {
        for (String ref : this.tempReferences) {
            this.objectMap.remove(ref);
        }
        this.tempReferences.clear();
    }

    /**
     * Create a new reference with a new unique key.
     *
     * @param object
     * @return
     */
    @NativeJS
    String allocReference(Object object) {
        String key = referencePrequel + (nextKey++);
        this.addReference(key, object);
        return key;
    }

    /**
     * Add a reference to the this translator.
     *
     * @param reference
     * @param object
     */
    @NativeJS
    void addReference(String reference, Object object) {
        this.objectMap.put(reference, object);
    }

    /**
     * Determine if the JSON Encoded Reference is valid.
     *
     * @param reference
     * @return
     */
    @NativeJS
    boolean hasReference(String reference) {
        return objectMap.containsKey(reference);
    }

    /**
     * Determine if an object has been encoded (and saved) by this translator.
     *
     * @param object
     * @return
     */
    @NativeJS
    boolean hasReferredObject(Object object) {
        return objectMap.containsValue(object);
    }

    /**
     * Retrieve an object from a JSON encoded reference.
     *
     * @param reference
     * @return
     */
    @NativeJS
    public Object getReferredObject(String reference) throws UnknownReferenceException {
        if (!objectMap.containsKey(reference)) {
            throw new UnknownReferenceException(reference);
        }
        return objectMap.get(reference);
    }

    /**
     * Return a JSON encoded reference to the given object. The reference is
     * only valid for this translator.
     *
     * @param object
     * @return A JSON encoded reference
     */
    @NativeJS
    public String getReference(Object object) throws UntrackedObjectException {
        if (!this.hasReferredObject(object)) throw new UntrackedObjectException(object);
        return objectMap.getKey(object);
    }

    /**
     * Return a collection of all objects tracked by this Translator.
     *
     * @return
     */
    @NativeJS
    public Collection<Object> getAllTrackedObjects() {
        Collection<Object> values = this.objectMap.values();
        return new ArrayList<>(values);
    }

    /**
     * Remove object from this translator.  On subsequent encodes a new full
     * encoding will take place.  If the object is not tracked by this 
     * translator, an exception will be thrown.
     * @param object
     * @return The reference to the object.
     */
    public String removeTrackedObject(Object object) throws UntrackedObjectException{
        if (!this.hasReferredObject(object)) throw new UntrackedObjectException(object);
        String reference = this.getReference(object);
        this.objectMap.remove(objectMap.getKey(object));
        return reference;
    }
    
    /**
     * Clear all memory of sent objects. Use if the client of a websocket
     * refreshes the browser before resending objects. Does not reset the
     * nextKey variable in case the client hasn't cleared it's map.
     *
     * @return All objects previously in memory.
     */
    @NativeJS
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
    @NativeJS
    public final TranslatorResult encode(Object object) throws JJJRMIException {
        return new TranslatorResult(this).encodeFromObject(object);
    }

    /**
     * Translate a JSON encoded object to a POJO, returning the reference if it
     * has previously been stored.
     *
     * @param encodedResult current encode context
     * @return a new object
     * @throws ca.frar.jjjrmi.exceptions.DecoderException
     */
    @NativeJS
    public final TranslatorResult decode(String source) throws DecoderException {
        return new TranslatorResult(this).decodeFromString(source);
    }

    @NativeJS
    public void addEncodeListener(Consumer<Object> lst) {
        this.encodeListeners.add(lst);
    }

    @NativeJS
    public void notifyEncode(Object object) {
        for (Consumer<Object> encodeListener : this.encodeListeners) encodeListener.accept(object);
    }

    /**
     * Determine the number of referenced objects.
     *
     * @return
     */
    @NativeJS
    public int size() {
        return objectMap.size();
    }
}
