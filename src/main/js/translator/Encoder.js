"use strict";
const Constants = require("./Constants");
const TranslatorResult = require("./TranslatorResult");
const HandlerFactory = require("./HandlerFactory");
const EncodedReference = require("./EncodedReference");
const EncodedPrimitive = require("./EncodedPrimitive");
const EncodedEnum = require("./EncodedEnum");
const EncodedNull = require("./EncodedNull");

class EncodedArray {
    constructor(object, encodedResult) {
        this.encodedResult = encodedResult;
        this.object = object;
        this.json = {};
        this.elements = [];

        this.json[Constants.KeyParam] = encodedResult.getTranslator().allocReference(object);
        this.json[Constants.RetainParam] = false;
        this.json[Constants.SizeParam] = object.length;
        encodedResult.getTranslator().addTempReference(this[Constants.KeyParam], object);
        this.json[Constants.ElementsParam] = this.elements;
        this.encode();
    }
    encode() {
        for (let i = 0; i < this.object.length; i++) {
            let element = this.object[i];
            if (element !== undefined) {
                this.elements[i] = new Encoder(element, this.encodedResult).encode();
            }
        }
    }

    toString() {
        return JSON.stringify(this.json, null, 2);
    }        

    toJSON() {
        return this.json;
    }
}
;

class EncodedObject {
    constructor(object, encodedResult) {
        this.object = object;
        this.json = {};
        this.encodedResult = encodedResult;

        this.json = {};
        this.json[Constants.KeyParam] = encodedResult.getTranslator().allocReference(object);
        this.json[Constants.TypeParam] = object.constructor.__getClass ? object.constructor.__getClass() : null;
        this.json[Constants.FieldsParam] = {};
    }
    encode() {
        for (let field in this.object) {
            if (this.object[field] === undefined) continue;
            let encoded = new Encoder(this.object[field], this.encodedResult).encode();
            this.json[Constants.FieldsParam][field] = encoded;
        }
        this.encodedResult.getTranslator().notifyEncode(this.object);
    }

    toString() {
        return JSON.stringify(this.json, null, 2);
    }

    toJSON() {
        return this.json;
    }

    setField(field) {
        let toJSON = new Encoder(field.get(this.object), this.encodedResult).encode();
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
    constructor(object, encodedResult) {
        this.object = object;
        this.encodedResult = encodedResult;
    }
    encode() {
        if (this.object === null) {
            return new EncodedNull();
        } else if (typeof this.object === "number" || typeof this.object === "string" || typeof this.object === "boolean") {
            return new EncodedPrimitive(this.object);
        } else if (this.encodedResult.getTranslator().hasReferredObject(this.object)) {
            return new EncodedReference(this.encodedResult.getTranslator().getReference(this.object));
        } else if (this.object instanceof Array) {
            return new EncodedArray(this.object, this.encodedResult);
        } else if (this.object.constructor.__isEnum()) {
            return new EncodedEnum(this.object);
        } else if (HandlerFactory.getInstance().hasHandler(this.object.constructor.__getClass())) {
            let handlerClass = HandlerFactory.getInstance().getHandler(this.object.constructor.__getClass());
            let handler = handlerClass.getConstructor(TranslatorResult.class).newInstance(this.encodedResult);
            let encodedObject = handler.doEncode(this.object);
            this.encodedResult.put(encodedObject);
            return new EncodedReference(this.encodedResult.getTranslator().getReference(this.object));
        } else {
            let encodedObject = new EncodedObject(this.object, this.encodedResult);
            this.encodedResult.put(encodedObject);
            encodedObject.encode();
            return new EncodedReference(this.encodedResult.getTranslator().getReference(this.object));
        }
    }
}

module.exports = {
    Encoder: Encoder,
    EncodedObject: EncodedObject,
    EncodedArray: EncodedArray
};