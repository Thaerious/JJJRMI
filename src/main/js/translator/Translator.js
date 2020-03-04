"use strict";
const TranslatorResult = require("./TranslatorResult");
const ClassRegistry = require("./ClassRegistry");
const HandlerRegistry = require("./HandlerRegistry");
const BiMap = require("./BiMap");

class Translator {
    constructor() {
        this.handlers = new Map();
        this.referenceListeners = [];
        this.objectMap = new BiMap();
        this.tempReferences = [];
        this.nextKey = 0;
        this.classRegistry = new ClassRegistry();
        this.handlerRegistry = new HandlerRegistry();
    }

    registerPackage(pkg){
        this.classRegistry.registerPackage(pkg);
        this.handlerRegistry.registerPackage(pkg);
    }
    
    /**
     * Notify listeners whenever a tracked object is added.
     * @param {type} lst
     * @returns {undefined}
     */
    addReferenceListener(lst) {
        this.referenceListeners.push(lst);
    }
    addReference(reference, object) {
        this.objectMap.put(reference, object);
        for (let listener of this.referenceListeners){
            listener(object);
        }
    }
    addTempReference(reference, object) {
        this.objectMap.put(reference, object);
        this.tempReferences.push(reference);
    }
    allocReference(object, retain = true) {
        let key = Translator.referencePrequel + this.nextKey++;
        if (retain){
            this.addReference(key, object);
        } else {
            this.addTempReference(key, object);
        }
        return key;
    }
    clear() {
        this.objectMap = new BiMap();
        this.tempReferences = [];
    }
    clearTempReferences() {
        for (let ref of this.tempReferences) {
            this.objectMap.removeByKey(ref);
        }
        this.tempReferences = [];
    }
    decode(source) {
        let result = new TranslatorResult(this).decodeFromString(source);
        result.finalizeRoot();
        this.clearTempReferences();
        return result;
    }
    encode(object) {
        let result = new TranslatorResult(this).encodeFromObject(object);
        result.finalizeRoot();
        this.clearTempReferences();
        return result;
    }
    getAllReferredObjects() {
        let allReferredObjects = [];
        for (let v of this.objectMap.values()) allReferredObjects.push(v);
        return allReferredObjects;
    }
    getReference(object) {
        return this.objectMap.getKey(object);
    }
    getReferredObject(reference) {
        if (!this.objectMap.containsKey(reference)){
            throw new Error("MissingReferenceException: " + reference);
        }
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
    size() {
        return this.objectMap.size();
    }
}

Translator.referencePrequel = "C";
if (typeof module !== "undefined") module.exports = Translator;
