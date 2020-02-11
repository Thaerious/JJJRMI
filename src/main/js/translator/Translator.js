"use strict";
const BiMap = require("./BiMap");
const EncodedResult = require("./EncodedResult");
const HandlerFactory = require("./HandlerFactory");
const EncodedObject = require("./EncodedObject");

class Translator {
    constructor() {
        this.handlers = new Map();
        this.encodeListeners = [];
        this.decodeListeners = [];
        this.objectMap = new BiMap();
        this.tempReferences = [];
        this.nextKey = 0;
    }
    addReference(reference, object) {
        this.objectMap.put(reference, object);
    }
    addTempReference(reference, object) {
        this.objectMap.put(reference, object);
        this.tempReferences.add(reference);
    }
    allocReference(object) {
        let key = Translator.referencePrequel + this.nextKey++;
        this.addReference(key, object);
        return key;
    }
    clear() {
        let values = this.objectMap.values().toArray();
        this.objectMap.clear();
        this.tempReferences.clear();
        return values;
    }
    clearTempReferences() {
        for (let ref of this.tempReferences) {
            this.objectMap.remove(ref);
        }
        this.tempReferences = [];
    }
    decode(encodedResult) {
        let list = new ArrayList();
        for (let jsonObject of encodedResult.getAllObjects()) {
            let key = jsonObject.getString(Constants.KeyParam);
            if (this.hasReference(key)) continue;

            list.add(new ObjectDecoder(encodedResult, jsonObject, this));
        }
        for (let decoder of list) {
            decoder.makeReady();
        }
        for (let decoder of list) {
            decoder.decode();
        }
        return this.getReferredObject(encodedResult.getRoot());
    }
    encode(object) {
        if (object === null) throw new Error("ca.frar.jjjrmi.exceptions.RootException");
        let encodedResult = new EncodedResult(this);
        
        if (this.hasReferredObject(object)) {
            encodedResult.setRoot(this.getReference(object));
            return encodedResult;
        } else if (HandlerFactory.getInstance().hasHandler(object.constructor.__getClass())) {
            this.encodeHandled(object, encodedResult);
        } else {
            this.encodeUnhandled(object, encodedResult);
        }

        return encodedResult;
    }
    encodeHandled(object, encodedResult) {
        try {
            let handlerClass = HandlerFactory.getInstance().getHandler(object.constructor.__getClass());
            let handler = handlerClass.getConstructor(EncodedResult.class).newInstance(encodedResult);
            let encodedObject = handler.doEncode(object);
            encodedResult.put(encodedObject);
            encodedResult.setRoot(this.getReference(object));
            this.clearTempReferences();
        } catch (ex) {
            throw new Error("ca.frar.jjjrmi.exceptions.EncoderException");
        }

    }
    encodeUnhandled(object, encodedResult) {
        let encodedObject = new EncodedObject(object, encodedResult);
        encodedResult.put(encodedObject);
        encodedResult.setRoot(this.getReference(object));
        encodedObject.encode();
        this.clearTempReferences();
    }
    getAllReferredObjects() {
        let values = this.objectMap.values();
        return new ArrayList(values);
    }
    getReference(object) {
        return this.objectMap.getKey(object);
    }
    getReferredObject(reference) {
        if (!this.objectMap.containsKey(reference)) throw new Error("ca.frar.jjjrmi.exceptions.MissingReferenceException");

        return this.objectMap.get(reference);
    }
    hasReference(reference) {
        return this.objectMap.containsKey(reference);
    }
    hasReferredObject(object) {
        return this.objectMap.containsValue(object);
    }
    removeByValue(obj) {
        if (!this.objectMap.containsValue(obj)) return false;

        this.objectMap.remove(this.objectMap.getKey(obj));
        return true;
    }
    
    notifyEncode(object){
    }
    
    notifyDecode(object){
    }
}
;
Translator.nextKey = 0;
Translator.referencePrequel = "C";

if (typeof module !== "undefined") module.exports = Translator;