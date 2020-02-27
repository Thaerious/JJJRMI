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
    encodeField(name, value) {
        let toJSON = new Encoder(value, this.translatorResult).encode();
        this.encodedObject.setFieldData(name, toJSON);
    }
    doEncode(object) {
        this.encodedObject = new EncodedObject(object, this.translatorResult);
        this.encode(object);
        return this.encodedObject;
    }
    doDecode(t, json) {
        this.json = json;
        this.decode(t);
        return t;
    }
    doGetInstance() {
        this.instance = this.getInstance();
        return this.instance;
    }
}

if (typeof module !== "undefined") module.exports = AHandler;