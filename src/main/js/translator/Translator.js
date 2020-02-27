"use strict";
const TranslatorResult = require("./TranslatorResult");
const ClassRegistry = require("./ClassRegistry");
const HandlerRegistry = require("./HandlerRegistry");
const BiMap = require("./BiMap");

class Translator {
    constructor() {
        this.handlers = new Map();
        this.encodeListeners = [];
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
    addEncodeListener(lst) {
        this.encodeListeners.add(lst);
    }
    addReference(reference, object) {
        this.objectMap.put(reference, object);
    }
    addTempReference(reference, object) {
        this.objectMap.put(reference, object);
        this.tempReferences.push(reference);
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
            this.objectMap.removeByKey(ref);
        }
        this.tempReferences = [];
    }
    decode(source) {                
        return new TranslatorResult(this).decodeFromString(source);
    }
    encode(object) {
        return new TranslatorResult(this).encodeFromObject(object);
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
        if (!this.objectMap.containsKey(reference)) throw new Error("ca.frar.jjjrmi.exceptions.MissingReferenceException");
        return this.objectMap.get(reference);
    }
    hasReference(reference) {
        return this.objectMap.containsKey(reference);
    }
    hasReferredObject(object) {
        return this.objectMap.containsValue(object);
    }
    notifyEncode(object) {
        for (let encodeListener of this.encodeListeners) {
            encodeListener.accept(object);
        }
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
