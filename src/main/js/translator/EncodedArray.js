"use strict";
const Array = require("./Array");
const Constants = require("./Constants");
const JSONArray = require("./JSONArray");
class EncodedArray {
	constructor(object, encodedResult) {
            this.encodedResult = encodedResult;
            this.object = object;
            this.elements = [];
            this[Constants.KeyParam] = encodedResult.getTranslator().allocReference(object);
            this[Constants.RetainParam] = false;
            encodedResult.getTranslator().addTempReference(this[Constants.KeyParam], object);
            this[Constants.ElementsParam] = this.elements;
            this.encode();
        }
	encode() {
            for(let i = 0; i < this.object.length; i++){
                let element = this.object[i];
                this.elements[i] = new Encoder(element, this.encodedResult).encode();
            }
        }
};

if (typeof module !== "undefined") module.exports = EncodedArray;