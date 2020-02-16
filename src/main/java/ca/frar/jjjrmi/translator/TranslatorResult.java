/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.translator;

import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.exceptions.MissingReferenceException;
import ca.frar.jjjrmi.exceptions.RootException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import org.json.JSONObject;

/**
 * An array (wrapped) of encoded objects. The first object is the target object,
 * all other object are object that have not been previously translated.
 *
 * @author Ed Armstrong
 */
@JJJ(insertJJJMethods = false)
public class TranslatorResult {
    private final Translator translator;
    private JSONObject json;

    TranslatorResult(Translator translator) {
        this.translator = translator;
    }

    TranslatorResult encodeFromObject(Object object) throws EncoderException{
        if (object == null) throw new RootException();
        this.json = new JSONObject();
        this.json.put(Constants.NewObjects, new JSONObject());

        if (this.translator.hasReferredObject(object)) {
            this.setRoot(this.translator.getReference(object));
        } else if (HandlerFactory.getInstance().hasHandler(object.getClass())) {
            encodeHandled(object);
        } else {
            encodeUnhandled(object);
        }        
        
        this.translator.clearTempReferences();
        return this;
    }

    @NativeJS
    private void encodeHandled(Object object) throws EncoderException {
        try {
            Class<? extends AHandler<?>> handlerClass = HandlerFactory.getInstance().getHandler(object.getClass());
            AHandler<?> handler = handlerClass.getConstructor(TranslatorResult.class).newInstance(this);
            EncodedObject encodedObject = handler.doEncode(object);
            this.put(encodedObject);
            this.setRoot(this.translator.getReference(object));
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new EncoderException(ex, object);
        }
    }

    @NativeJS
    private void encodeUnhandled(Object object) throws EncoderException {
        try {
            EncodedObject encodedObject = new EncodedObject(object, this);
            this.put(encodedObject);
            this.setRoot(this.translator.getReference(object));
            encodedObject.encode();
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new EncoderException(ex, object);
        }
    }

    TranslatorResult decodeFromString(String source) throws DecoderException {
        this.json = new JSONObject(source);
        ArrayList<ObjectDecoder> list = new ArrayList<>();
        JSONObject newObjects = this.json.getJSONObject(Constants.NewObjects);

        for (String key : newObjects.keySet()) {
            if (this.translator.hasReference(key)) continue;
            list.add(new ObjectDecoder(this, newObjects.getJSONObject(key)));
        }
        for (ObjectDecoder decoder : list) {
            decoder.makeReady();
        }
        for (ObjectDecoder decoder : list) {
            decoder.decode();
        }

        return this;
    }

    /**
     * @return the translator
     */
    @NativeJS
    Translator getTranslator() {
        return translator;
    }

    @NativeJS
    void setRoot(String rootKey) {
        this.json.put(Constants.RootObject, rootKey);
    }

    @NativeJS
    public Object getRoot() throws MissingReferenceException {
        String key = this.json.getString(Constants.RootObject);
        return this.translator.getReferredObject(key);        
    }

    @NativeJS
    void put(EncodedObject encodedObject) {
        this.json.getJSONObject(Constants.NewObjects).put(encodedObject.getKey(), encodedObject);
    }
    
    @NativeJS
    /**
     * Returns the number of new objects in this result.  
     * @return the number of new objects.
     */
    public int newObjectCount(){
        return this.json.getJSONObject(Constants.NewObjects).keySet().size();
    }
    
    @Override
    public String toString(){
        return this.json.toString();
    }
    
    public String toString(int indentFactor){
        return this.json.toString(indentFactor);
    }    
}
