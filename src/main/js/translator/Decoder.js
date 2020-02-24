"use strict";
const Constants = require("./Constants");
const ArrayDecoder = require("./ArrayDecoder");

class Decoder {
    constructor(json, translator) {
        this.json = json;
        this.translator = translator;
    }
    decode() {
        if (this.json[Constants.TypeParam] === Constants.NullValue){
            return null;
        }
        else if (this.json[Constants.PointerParam]){
            let pointer = this.json[Constants.PointerParam];
            return this.translator.getReferredObject(pointer);
        }
        else if (this.json[Constants.EnumParam]) {
            let aClass = this.translator.classRegistry.getClass(this.json[Constants.EnumParam]);  
            let value = this.json[Constants.ValueParam];
            return aClass[value];
        }
        else if (this.json[Constants.ValueParam]) {
            return this.json[Constants.ValueParam];
        }
        else if (this.json[Constants.ElementsParam]){
            return new ArrayDecoder(this.json, this.translator).decode();
        }

        throw new Error("ca.frar.jjjrmi.exceptions.UnknownEncodingException");
    }
}

if (typeof module !== "undefined") module.exports = Decoder;