/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.translator;

import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.JSParam;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.exceptions.UnknownReferenceException;
import ca.frar.jjjrmi.exceptions.RootException;
import ca.frar.jjjrmi.utility.JJJOptionsHandler;
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
    private Object root;

    @NativeJS
    TranslatorResult(Translator translator) {
        this.translator = translator;
    }

    @NativeJS
    TranslatorResult encodeFromObject(Object object) throws JJJRMIException{
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

        return this;
    }

    @NativeJS
    private void encodeHandled(Object object) throws JJJRMIException {
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
    private void encodeUnhandled(Object object) throws JJJRMIException {
        try {
            EncodedObject encodedObject = new EncodedObject(object, this, new JJJOptionsHandler(object).retain());
            this.put(encodedObject);
            this.setRoot(this.translator.getReference(object));
            encodedObject.encode();
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new EncoderException(ex, object);
        }
    }

    @NativeJS
    TranslatorResult decodeFromString(String source) throws DecoderException {
        this.json = new JSONObject(source);
        ArrayList<ObjectDecoder> list = new ArrayList<>();
        JSONObject newObjects = this.json.getJSONObject(Constants.NewObjects);

        for (String key : newObjects.keySet()) {
            if (this.translator.hasReference(key)) continue;
            JSONObject jsonObject = newObjects.getJSONObject(key);
            list.add(new ObjectDecoder(this, jsonObject));
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

    void finalizeRoot() throws UnknownReferenceException{
        String key = this.json.getString(Constants.RootObject);
        this.root = this.translator.getReferredObject(key);
    }

    @NativeJS
    public Object getRoot() throws UnknownReferenceException {
        return this.root;
    }

    @NativeJS
    void put(EncodedObject encodedObject) {
        String key = encodedObject.getKey();
        JSONObject toJSON = encodedObject.toJSON();
        this.json.getJSONObject(Constants.NewObjects).put(key, toJSON);
    }

    @NativeJS
    /**
     * Returns the number of new objects in this result.
     * @return the number of new objects.
     */
    public int newObjectCount(){
        return this.json.getJSONObject(Constants.NewObjects).keySet().size();
    }

    public JSONObject toJSON(){
        return this.json;
    }

    public String toString(){
        return this.json.toString();
    }

    @NativeJS
    @JSParam(name="indentFactor", init="0")
    public String toString(int indentFactor){
        return this.json.toString(indentFactor);
    }
}
