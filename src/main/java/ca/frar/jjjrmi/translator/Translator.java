package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.translator.encoder.AHandler;
import ca.frar.jjjrmi.translator.decoder.ObjectDecoder;
import ca.frar.jjjrmi.translator.encoder.EncodedResult;
import ca.frar.jjjrmi.translator.encoder.EncodedObject;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.exceptions.MissingReferenceException;
import ca.frar.jjjrmi.exceptions.RootException;
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
     * Remove the referred object from the translator.
     * @param obj
     * @return 
     */
    @NativeJS
    public boolean removeByValue(Object obj) {
        if (!objectMap.containsValue(obj)) {
            return false;
        }
        this.objectMap.remove(objectMap.getKey(obj));
        return true;
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
    @NativeJS
    public void addTempReference(String reference, Object object) {
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
    public String allocReference(Object object) {
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
    public void addReference(String reference, Object object) {
        this.objectMap.put(reference, object);
    }

    /**
     * Determine if the JSON Encoded Reference is valid.
     *
     * @param reference
     * @return
     */
    @NativeJS
    public boolean hasReference(String reference) {
        return objectMap.containsKey(reference);
    }

    /**
     * Determine if an object has been encoded (and saved) by this translator.
     *
     * @param object
     * @return
     */
    @NativeJS
    public boolean hasReferredObject(Object object) {
        return objectMap.containsValue(object);
    }

    /**
     * Retrieve an object from a JSON encoded reference.
     *
     * @param reference
     * @return
     */
    @NativeJS
    public Object getReferredObject(String reference) throws MissingReferenceException {
        if (!objectMap.containsKey(reference)) {
            throw new MissingReferenceException(reference);
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
    public String getReference(Object object) {
        return objectMap.getKey(object);
    }

    /**
     * Return a collection of all objects tracked by this Translator.
     *
     * @return
     */
    @NativeJS
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
    public final EncodedResult encode(Object object) throws EncoderException {
        if (object == null) throw new RootException();
        EncodedResult encodedResult = new EncodedResult(this);

        if (this.hasReferredObject(object)) {
            encodedResult.setRoot(this.getReference(object));
            return encodedResult;
        } else if (HandlerFactory.getInstance().hasHandler(object.getClass())) {
            encodeHandled(object, encodedResult);
        } else {
            encodeUnhandled(object, encodedResult);
        }
        return encodedResult;
    }

    @NativeJS
    private void encodeHandled(Object object, EncodedResult encodedResult) throws EncoderException {
        try {
            Class<? extends AHandler<?>> handlerClass = HandlerFactory.getInstance().getHandler(object.getClass());
            AHandler<?> handler = handlerClass.getConstructor(EncodedResult.class).newInstance(encodedResult);
            EncodedObject encodedObject = handler.doEncode(object);
            encodedResult.put(encodedObject);
            encodedResult.setRoot(this.getReference(object));
            this.clearTempReferences();
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new EncoderException(ex, object);
        }
    }

    @NativeJS
    private void encodeUnhandled(Object object, EncodedResult encodedResult) throws EncoderException {
        try {
            EncodedObject encodedObject = new EncodedObject(object, encodedResult);
            encodedResult.put(encodedObject);
            encodedResult.setRoot(this.getReference(object));
            encodedObject.encode();
            this.clearTempReferences();
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new EncoderException(ex, object);
        }
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
    public final Object decode(EncodedResult encodedResult) throws DecoderException {
        ArrayList<ObjectDecoder> list = new ArrayList<>();        
        
        for (JSONObject jsonObject : encodedResult.getAllObjects()) {
            String key = jsonObject.getString(Constants.KeyParam);
            if (this.hasReference(key)) continue;
            list.add(new ObjectDecoder(encodedResult, jsonObject, this));
        }
        for (ObjectDecoder decoder : list) {
            decoder.makeReady();
        }
        for (ObjectDecoder decoder : list) {
            decoder.decode();
        }

        return this.getReferredObject(encodedResult.getRoot());
    }

    public final Object decode(String source) throws DecoderException {
        return decode(new EncodedResult(this, source));
    }

    public void addEncodeListener(Consumer<Object> lst) {
        this.encodeListeners.add(lst);
    }

    public void addDecodeListener(Consumer<Object> lst) {
        this.decodeListeners.add(lst);
    }

    public void notifyEncode(Object object) {
        for (Consumer<Object> encodeListener : this.encodeListeners) encodeListener.accept(object);
    }

    public void notifyDecode(Object object) {
        for (Consumer<Object> decodeListener : this.decodeListeners) decodeListener.accept(object);
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
