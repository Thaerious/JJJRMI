package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.exceptions.UntrackedObjectException;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.exceptions.UnknownReferenceException;
import ca.frar.jjjrmi.utility.BiMap;
import ca.frar.jjjrmi.utility.JJJOptionsHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Consumer;

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
public class Translator {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JJJRMI");
    private String referencePrequel = "S";
    private final ArrayList<Consumer<Object>> referenceListeners = new ArrayList<>();
    private final BiMap<String, Object> objectMap = new BiMap<>();
    private final ArrayList<String> tempReferences = new ArrayList<>();
    private int nextKey = 0;

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

    public void setReferencePrequel(String referencePrequel){
        this.referencePrequel = referencePrequel;
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
     * Create a new reference with a new unique key.
     *
     * @param object
     * @return
     */
    String allocReference(Object object) {
        return this.allocReference(object, true);
    }

    /**
     * Create a new reference with a new unique key.
     *
     * @param object
     * @return
     */
    String allocReference(Object object, boolean isRetained) {
        String key = referencePrequel + (nextKey++);

        if (new JJJOptionsHandler(object).retain()){
            this.addReference(key, object);
        } else {
            this.addTempReference(key, object);
        }

        return key;
    }

    /**
     * Add a reference to the this translator.
     *
     * @param reference
     * @param object
     */
    void addReference(String reference, Object object) {
        this.objectMap.put(reference, object);
        for (Consumer<Object> lst : this.referenceListeners){
            lst.accept(object);
        }
    }

    /**
     * Determine if the JSON Encoded Reference is valid.
     *
     * @param reference
     * @return
     */
    boolean hasReference(String reference) {
        return objectMap.containsKey(reference);
    }

    /**
     * Determine if an object has been encoded (and saved) by this translator.
     *
     * @param object
     * @return
     */
    boolean hasReferredObject(Object object) {
        return objectMap.containsValue(object);
    }

    /**
     * Retrieve an object from a JSON encoded reference.
     *
     * @param reference
     * @return
     */
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
    public String getReference(Object object) throws UntrackedObjectException {
        if (!this.hasReferredObject(object)) throw new UntrackedObjectException(object);
        return objectMap.getKey(object);
    }

    /**
     * Return a collection of all objects tracked by this Translator.
     *
     * @return
     */
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
    public final TranslatorResult encode(Object object) throws JJJRMIException {
        TranslatorResult encodeFromObject = new TranslatorResult(this).encodeFromObject(object);
        encodeFromObject.finalizeRoot();
        this.clearTempReferences();
        return encodeFromObject;
    }

    /**
     * Translate a JSON encoded object to a POJO, returning the reference if it
     * has previously been stored.
     *
     * @param encodedResult current encode context
     * @return a new object
     * @throws ca.frar.jjjrmi.exceptions.DecoderException
     */
    public final TranslatorResult decode(String source) throws DecoderException {
        TranslatorResult decodeFromString = new TranslatorResult(this).decodeFromString(source);
        decodeFromString.finalizeRoot();
        this.clearTempReferences();
        return decodeFromString;
    }

    public void addReferenceListener(Consumer<Object> lst) {
        this.referenceListeners.add(lst);
    }

    public void removeReferenceListener(Consumer<Object> lst) {
        this.referenceListeners.remove(lst);
    }    
    
    /**
     * Determine the number of referenced objects.
     *
     * @return
     */
    public int size() {
        return objectMap.size();
    }
}
