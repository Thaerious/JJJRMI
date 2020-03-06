"use strict";
const Constants = require("./Constants");
const TranslatorResult = require("./TranslatorResult");
const EncodedReference = require("./EncodedReference");
const EncodedPrimitive = require("./EncodedPrimitive");
const EncodedEnum = require("./EncodedEnum");
const EncodedNull = require("./EncodedNull");

class EncodedArray {
    constructor(object, translatorResult) {
        this.translatorResult = translatorResult;
        this.object = object;
        this.json = {};
        this.elements = [];

        this.json[Constants.KeyParam] = translatorResult.getTranslator().allocReference(object);
        this.json[Constants.RetainParam] = false;
        this.json[Constants.SizeParam] = object.length;
        translatorResult.getTranslator().addTempReference(this[Constants.KeyParam], object);
        this.json[Constants.ElementsParam] = this.elements;
        this.encode();
    }
    encode() {
        for (let i = 0; i < this.object.length; i++) {
            let element = this.object[i];
            if (element !== undefined) {
                this.elements[i] = new Encoder(element, this.translatorResult).encode();
            }
        }
    }
    toJSON() {
        return this.json;
    }
}

class EncodedObject {
    constructor(object, translatorResult, retain = true) {
        this.object = object;
        this.json = {};
        this.translatorResult = translatorResult;

        this.json = {};
        this.json[Constants.KeyParam] = translatorResult.getTranslator().allocReference(object, retain);
        this.json[Constants.TypeParam] = object.constructor.__getClass ? object.constructor.__getClass() : null;
        this.json[Constants.FieldsParam] = {};
    }
    encode() {
        for (let field in this.object) {
            if (this.object[field] === undefined) continue;
            if (field === "__jjjrmi") continue;
            let encoded = new Encoder(this.object[field], this.translatorResult).encode();
            this.json[Constants.FieldsParam][field] = encoded;
        }
    }
    toJSON() {
        return this.json;
    }
    setField(field) {
        let toJSON = new Encoder(field.get(this.object), this.translatorResult).encode();
        this.setFieldData(field.getName(), toJSON);
    }
    setFieldData(name, json) {
        this.json[Constants.FieldsParam][name] = json;
    }
    getField(fieldName) {
        return this.json[Constants.FieldsParam][fieldName];
    }
    getType() {
        return this.json[Constants.TypeParam];
    }
    getKey() {
        return this.json[Constants.KeyParam];
    }
}


class Encoder {
    constructor(object, translatorResult) {
        this.object = object;
        this.translatorResult = translatorResult;

    }
    encode() {
        let translator = this.translatorResult.getTranslator();

        if (this.object === null) {
            return new EncodedNull();
        } else if (typeof this.object === "number" || typeof this.object === "string" || typeof this.object === "boolean") {
            return new EncodedPrimitive(this.object);
        } else if (this.translatorResult.getTranslator().hasReferredObject(this.object)) {
            return new EncodedReference(this.translatorResult.getTranslator().getReference(this.object));
        } else if (this.object instanceof Array) {
            return new EncodedArray(this.object, this.translatorResult);
        } else if (translator.handlerRegistry.hasClass(this.object.constructor.__getClass())) {
            let handlerClass = translator.handlerRegistry.getClass(this.object.constructor.__getClass());
            let handler = new handlerClass(this.translatorResult);
            let encodedObject = handler.doEncode(this.object);
            this.translatorResult.put(encodedObject);
            return new EncodedReference(this.translatorResult.getTranslator().getReference(this.object));
        } else if (this.object.constructor.__isEnum()) {
            return new EncodedEnum(this.object);
        } else {
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