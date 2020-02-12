"use strict";
const BiMap = require("./BiMap");
const EncodedResult = require("./EncodedResult");
const HandlerFactory = require("./HandlerFactory");
const Constants = require("./Constants");
const EncodedObject = require("./Encoder").EncodedObject;
const ObjectDecoder = require("./ObjectDecoder");
const ClassRegistry = require("./ClassRegistry");

class Translator {
    constructor() {
        this.handlers = new Map();
        this.encodeListeners = [];
        this.decodeListeners = [];
        this.objectMap = new BiMap();
        this.tempReferences = [];
        this.nextKey = 0;
        this.classRegistry = new ClassRegistry();
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
        this.objectMap = new BiMap();
        this.tempReferences = [];
    }
    clearTempReferences() {
        for (let ref of this.tempReferences) {
            this.objectMap.remove(ref);
        }
        this.tempReferences = [];
    }
    decode(encodedResult) {
        let list = [];
        for (let jsonObject of encodedResult.getAllObjects()) {
            let key = jsonObject[Constants.KeyParam];
            if (this.hasReference(key)) continue;

            list.push(new ObjectDecoder(encodedResult, jsonObject, this));
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
        let handlerClass = HandlerFactory.getInstance().getHandler(object.constructor.__getClass());
        let handler = new handlerClass(encodedResult);
        let encodedObject = handler.doEncode(object);
        encodedResult.put(encodedObject);
        encodedResult.setRoot(this.getReference(object));
        this.clearTempReferences();
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
    notifyEncode(object) {
    }
    notifyDecode(object) {
    }
}
;
Translator.nextKey = 0;
Translator.referencePrequel = "C";

if (typeof module !== "undefined") module.exports = Translator;