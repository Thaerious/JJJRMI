"use strict";
const Constants = require("./Constants");
const Encoder = require("./Encoder");

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

if (typeof module !== "undefined") module.exports = EncodedObject;