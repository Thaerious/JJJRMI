"use strict";
const Constants = require("./Constants");
const HandlerFactory = require("./HandlerFactory");
const Decoder = require("./Decoder").Decoder;

class ObjectDecoder {
    constructor(translatorResult, json, translator) {
        this.json = json;
        if (!json.type) throw new Error();
        this.translator = translator;
        this.translatorResult = translatorResult;
    }
    decode() {
        if (HandlerFactory.getInstance().hasHandler(this.aClass)){
            this.handler.doDecode(this.result, this.json);
        }
        else {
            for (let fieldName in this.json[Constants.FieldsParam]){
                let fieldValue = new Decoder(this.json[Constants.FieldsParam][fieldName], this.translator).decode();
                this.result[fieldName] = fieldValue;
            }
        }
    }
    makeReady() {
        this.aClass = this.translator.classRegistry.getClass(this.json[Constants.TypeParam]);        
        if (this.aClass === null) throw new Error("ca.frar.jjjrmi.exceptions.UnknownClassException: " + this.json[Constants.TypeParam]);

        if (HandlerFactory.getInstance().hasHandler(this.aClass)) {
            let handlerClass = HandlerFactory.getInstance().getHandler(this.aClass);
            this.handler = handlerClass.getConstructor(EncodedResult.class).newInstance(this.translatorResult);
            this.result = this.handler.doGetInstance();
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