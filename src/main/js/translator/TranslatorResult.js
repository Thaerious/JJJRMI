"use strict";
const Constants = require("./Constants");
const EncodedObject = require("./Encoder").EncodedObject;
const ObjectDecoder = require("./ObjectDecoder");

class TranslatorResult {
    constructor(translator) {
        this.translator = translator;
    }
    decodeFromString(source) {
        this.json = JSON.parse(source);
        let list = [];
        let newObjects = this.json[Constants.NewObjects];

        for (let key in newObjects) {
            if (this.translator.hasReference(key)) continue;
            let jsonObject = newObjects[key];
            list.push(new ObjectDecoder(this, jsonObject, this.translator));
        }
        for (let decoder of list) {
            decoder.makeReady();
        }
        for (let decoder of list) {
            decoder.decode();
        }
        return this;
    }
    encodeFromObject(object) {
        let handlerRegistry = this.translator.handlerRegistry;

        this.json = {};
        this.json[Constants.NewObjects] = {};
        if (this.translator.hasReferredObject(object)){
            this.setRoot(this.translator.getReference(object));
        }
        else if (object.constructor.__getClass && handlerRegistry.hasClass(object.constructor.__getClass())){
            this.encodeHandled(object);
        }
        else{
            this.encodeUnhandled(object);
        }
        
        return this;
    }
    encodeHandled(object) {
        let handlerRegistry = this.translator.handlerRegistry;
        let handlerClass = handlerRegistry.getClass(object.constructor.__getClass());
        let handler = new handlerClass(this);
        let encodedObject = handler.doEncode(object);
        this.put(encodedObject);
        this.setRoot(this.translator.getReference(object));
    }
    encodeUnhandled(object) {
        let encodedObject = new EncodedObject(object, this, object.constructor.__isRetained());
        this.put(encodedObject);
        this.setRoot(this.translator.getReference(object));
        encodedObject.encode();
    }
    getRoot() {
        return this.root;
    }
    getTranslator() {
        return this.translator;
    }
    newObjectCount() {
        return this.json[Constants.NewObjects].keySet().size();
    }
    put(encodedObject) {
        let key = encodedObject.getKey();
        let toJSON = encodedObject.toJSON();
        this.json[Constants.NewObjects][key] = toJSON;
    }
    setRoot(rootKey) {
        this.json[Constants.RootObject] = rootKey;
    }
    finalizeRoot(){
        let key = this.json[Constants.RootObject];
        this.root = this.translator.getReferredObject(key);
    }    
    toString(indentFactor = 0) {
        return JSON.stringify(this.json, null, indentFactor);
    }
    toJSON(){
        return this.json;
    }
}

if (typeof module !== "undefined") module.exports = TranslatorResult;