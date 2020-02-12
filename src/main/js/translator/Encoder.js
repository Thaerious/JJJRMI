"use strict";
const Constants = require("./Constants");
const EncodedResult = require("./EncodedResult");
const HandlerFactory = require("./HandlerFactory");
const EncodedReference = require("./EncodedReference");
const EncodedPrimitive = require("./EncodedPrimitive");

class EncodedObject {
	constructor(object, encodedResult) {
            this.object = object;
            this.encodedResult = encodedResult;
            
            this.json = {};
            this.json[Constants.KeyParam] = encodedResult.getTranslator().allocReference(object);
            this.json[Constants.TypeParam] = object.constructor.__getClass();
            this.json[Constants.FieldsParam] = {};
        }
	encode() {
            for (let field in this.object){
                let encoded = new Encoder(this.object[field], this.encodedResult).encode();
                this.json[Constants.FieldsParam][field] = encoded;
            }
            this.encodedResult.getTranslator().notifyEncode(this.object);
        }
        
    toString(){
        return JSON.stringify(this, null, 2);
    }
    
    toJSON(){
        return this.json;
    }
};

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
            let handler = handlerClass.getConstructor(EncodedResult.class).newInstance(this.encodedResult);
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
  Encoder : Encoder,
  EncodedObject : EncodedObject
};