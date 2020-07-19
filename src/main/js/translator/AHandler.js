"use strict";
const Constants = require("./Constants");
const Encoder = require("./Encoder").Encoder;
const EncodedObject = require("./Encoder").EncodedObject;
const Decoder = require("./Decoder").Decoder;

class AHandler {
    constructor(translatorResult) {
        this.translatorResult = translatorResult;
        this.instance = undefined;
        this.encodedObject = undefined;
        this.json = undefined;
    }
    decodeField(jsonFieldName, pojoFieldName) {
        let jsonField = this.json[Constants.FieldsParam][jsonFieldName];
        let translator = this.translatorResult.getTranslator();
        let decoded = new Decoder(jsonField, translator).decode();
        this.instance[pojoFieldName] = decoded;
        return decoded;
    }
    decodeObject(jsonFieldName){
        let jsonField = this.json[Constants.FieldsParam][jsonFieldName];
        let translator = this.translatorResult.getTranslator();
        let decoded = new Decoder(jsonField, translator).decode();
        return decoded;        
    }
    encodeField(name, value) {
        let toJSON = new Encoder(value, this.translatorResult).encode();
        this.encodedObject.setFieldData(name, toJSON);
    }
    doEncode(object) {
        this.encodedObject = new EncodedObject(object, this.translatorResult, this.isRetained());
        this.encode(object); // abstract method call
        this.encodedObject.setType(this.constructor.__getHandles());
        return this.encodedObject;
    }
    doDecode(t, json) {
        this.json = json;
        this.decode(t); // abstract method call
        return t;
    }
    doGetInstance() {
        this.instance = this.getInstance();
        return this.instance;
    }
    isRetained(){
        return true;
    }
}

if (typeof module !== "undefined") module.exports = AHandler;