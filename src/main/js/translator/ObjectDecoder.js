"use strict";
const Constants = require("./Constants");
const HandlerFactory = require("./HandlerFactory");
const EncodedResult = require("./EncodedResult");

class ObjectDecoder {
    constructor(encodedResult, json, translator) {
        this.json = json;
        this.translator = translator;
        this.encodedResult = encodedResult;
    }
    decode() {
        if (HandlerFactory.getInstance().hasHandler(this.aClass)){
            this.handler.doDecode(this.result, this.json);
        }
        else {
            for (let fieldName of this.json[Constants.FieldsParam]){
                let fieldValue = new Decoder(this.json[Constants.FieldsParam][fieldName], this.translator).decode();
                this.result[fieldName] = fieldValue;
            }
        }
    }
    makeReady() {
        this.aClass = this.translator.classMap.get(this.json[Constants.TypeParam]);
        if (this.aClass === null) throw new Error("ca.frar.jjjrmi.exceptions.UnknownClassException");

        if (HandlerFactory.getInstance().hasHandler(this.aClass)) {
            let handlerClass = HandlerFactory.getInstance().getHandler(this.aClass);
            this.handler = handlerClass.getConstructor(EncodedResult.class).newInstance(this.encodedResult);
            this.result = this.handler.getInstance();
        } else {
            this.result = new this.aClass();
        }
        
        if (this.result.constructor.__isTransient()){
            this.translator.addTempReference(this.json[Constants.KeyParam], this.result);
        } else {
            this.translator.addReference(this.json[Constants.KeyParam], this.result);
        }
    }
}

if (typeof module !== "undefined") module.exports = ObjectDecoder;