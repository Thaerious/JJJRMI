"use strict";
const Constants = require("./Constants");
const TranslatorResult = require("./TranslatorResult");
const EncodedReference = require("./EncodedReference");
const EncodedPrimitive = require("./EncodedPrimitive");
const EncodedEnum = require("./EncodedEnum");
const EncodedNull = require("./EncodedNull");
const LOGGER = require("./Logger");

class EncodedArray {
    constructor(object, translatorResult) {
        this.translatorResult = translatorResult;
        this.object = object;
        this.json = {};
        this.elements = [];

        this.json[Constants.KeyParam] = translatorResult.getTranslator().allocReference(object, false);
        this.json[Constants.RetainParam] = false;
        this.json[Constants.SizeParam] = object.length;
        this.json[Constants.ElementsParam] = this.elements;
        this.encode();
        LOGGER.log("translator", `array @ ${this[Constants.KeyParam]}`);
    }
    encode() {
        for (let i = 0; i < this.object.length; i++) {
            let element = this.object[i];
            if (element !== undefined) {
                this.elements[i] = new Encoder(element, this.translatorResult).encode();
            }
            // else: probably should encode a null here
        }
    }
    toJSON() {
        return this.json;
    }
}

class EncodedObject {
    constructor(object, translatorResult, retain) {
        LOGGER.log("translator", `new EncodedObject(${object.constructor.name}, ${retain})`);
        this.object = object;
        this.json = {};
        this.translatorResult = translatorResult;

        this.json = {};
        this.json[Constants.KeyParam] = translatorResult.getTranslator().allocReference(object, retain);
        this.json[Constants.TypeParam] = object.constructor.__getClass ? object.constructor.__getClass() : null;
        this.json[Constants.FieldsParam] = {};
    }


     // Called by AHandler
    setType(javaCononicalName){
        this.json[Constants.TypeParam] = javaCononicalName;
    }

    encode() {
        for (let field in this.object) {
            LOGGER.log("translator", ` -- field ${field}`);

            if (this.object[field] === undefined){
                LOGGER.log("translator", ` -- ${field} : undefined`);
                continue;
            }
            if (field === "__jjjrmi"){
                LOGGER.log("translator", ` -- ${field} : skipped`);
                continue;
            }
            let encoded = new Encoder(this.object[field], this.translatorResult).encode();
            this.json[Constants.FieldsParam][field] = encoded;

            LOGGER.log("translator", ` -- ${field} : encoded`);
            LOGGER.log("translator+", encoded);
        }
    }
    toJSON() {
        return this.json;
    }
    setFieldData(name, json) {
        this.json[Constants.FieldsParam][name] = json;
    }
    getKey() {
        return this.json[Constants.KeyParam];
    }
}


class Encoder {
    constructor(object, translatorResult) {
        LOGGER.log("translator", `new Encoder(${object==null ? "null" : object.constructor.name})`);
        this.object = object;
        this.translatorResult = translatorResult;
    }
    encode() {
        let translator = this.translatorResult.getTranslator();

        if (this.object === null) {
            LOGGER.log("translator", `encoder.encode() : null`);
            return new EncodedNull();
        } else if (typeof this.object === "number" || typeof this.object === "string" || typeof this.object === "boolean") {
            LOGGER.log("translator", `encoder.encode() : primitive`);
            return new EncodedPrimitive(this.object);
        } else if (this.translatorResult.getTranslator().hasReferredObject(this.object)) {
            let ptr = this.translatorResult.getTranslator().getReference(this.object);
            LOGGER.log("translator", `encoder.encode() : reference ${ptr}`);
            return new EncodedReference(ptr);
        } else if (this.object instanceof Array) {
            LOGGER.log("translator", `encoder.encode() : array`);
            return new EncodedArray(this.object, this.translatorResult);
        } else if (translator.handlerRegistry.hasClass(this.object.constructor.__getClass())) {
            LOGGER.log("translator", `encoder.encode() : handled`);
            let handlerClass = translator.handlerRegistry.getClass(this.object.constructor.__getClass());
            let handler = new handlerClass(this.translatorResult);
            let encodedObject = handler.doEncode(this.object);
            this.translatorResult.put(encodedObject);
            return new EncodedReference(this.translatorResult.getTranslator().getReference(this.object));
        } else if (this.object.constructor.__isEnum()) {
            LOGGER.log("translator", `encoder.encode() : enum`);
            return new EncodedEnum(this.object);
        } else {
            LOGGER.log("translator", `encoder.encode() : object`);
            let encodedObject = new EncodedObject(this.object, this.translatorResult, this.object.constructor.__isRetained());
            this.translatorResult.put(encodedObject);
            encodedObject.encode();
            return new EncodedReference(this.translatorResult.getTranslator().getReference(this.object));
        }
    }
}

module.exports = {
    Encoder: Encoder,
    EncodedObject: EncodedObject,
    EncodedArray: EncodedArray
};