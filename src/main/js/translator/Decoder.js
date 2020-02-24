"use strict";
const Constants = require("./Constants");

class ArrayDecoder {
    constructor(json, translator) {
        if (json === null) throw new Error("java.lang.NullPointerException");
        if (translator === null) throw new Error("java.lang.NullPointerException");
        this.json = json;
        this.translator = translator;
    }
    decode() {
        let elements = this.json[Constants.ElementsParam];
        this.result = new Array(this.json[Constants.SizeParam]);
        this.translator.addTempReference(this.json[Constants.KeyParam], this.result);
        for (let i = 0; i < this.json[Constants.SizeParam]; i++) {
            let decoded = new Decoder(elements[i], this.translator).decode();
            this.result[i] = decoded;
        }

        return this.result;
    }
}

class Decoder {
    constructor(json, translator) {
        this.json = json;
        this.translator = translator;
    }
    decode() {
        if (this.json[Constants.TypeParam] === Constants.NullValue){
            return null;
        }
        else if (Constants.PointerParam in this.json){
            let pointer = this.json[Constants.PointerParam];
            return this.translator.getReferredObject(pointer);
        }
        else if (Constants.EnumParam in this.json) {
            let aClass = this.translator.classRegistry.getClass(this.json[Constants.EnumParam]);  
            let value = this.json[Constants.ValueParam];
            return aClass[value];
        }
        else if (Constants.ValueParam in this.json) {
            return this.json[Constants.ValueParam];
        }
        else if (Constants.ElementsParam in this.json){
            return new ArrayDecoder(this.json, this.translator).decode();
        }

        throw new Error("ca.frar.jjjrmi.exceptions.UnknownEncodingException: " + JSON.stringify(this.json));
    }
}

if (typeof module !== "undefined") module.exports = Decoder;

module.exports = {
    Decoder: Decoder,
    ArrayDecoder: ArrayDecoder
};