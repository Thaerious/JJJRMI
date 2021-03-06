"use strict";
const Constants = require("./Constants");
const Decoder = require("./Decoder").Decoder;

class ObjectDecoder {
    constructor(translatorResult, json, translator) {
        this.json = json;
        this.translator = translator;
        this.translatorResult = translatorResult;
    }
    decode() {
        if (this.handler){
            this.handler.doDecode(this.result, this.json);
        }
        else {
            for (let fieldName in this.json[Constants.FieldsParam]){
                let fieldValue = new Decoder(this.json[Constants.FieldsParam][fieldName], this.translator).decode();
                this.result[fieldName] = fieldValue;
            }
        }
    }

    afterDecode(){
    let decoder = null;

    if (this.handler) {
        decoder = this.handler;
    } else {
        decoder = this.result;
    }
    if (decoder.__afterDecode) decoder.__afterDecode();
}

    /**
     *
     * @returns {undefined}
     */
    makeReady() {
        if (this.json[Constants.TypeParam] === null){
            this.result = {};
            return;
        }

        let className = this.json[Constants.TypeParam];
        let retained = true;

        if (this.translator.handlerRegistry.hasClass(className)) {
            let handlerClass = this.translator.handlerRegistry.getClass(className);
            this.handler = new handlerClass(this.translatorResult);
            this.result = this.handler.doGetInstance();
            retained = this.handler.isRetained();
        } else {
            this.aClass = this.translator.classRegistry.getClass(className);
            if (this.aClass === null) throw new Error("ca.frar.jjjrmi.exceptions.UnknownClassException: " + this.json[Constants.TypeParam]);
            this.result = new this.aClass();
            retained = this.result.constructor.__isRetained();
        }

        if (retained){
            this.translator.addReference(this.json[Constants.KeyParam], this.result);
        } else {
            this.translator.addTempReference(this.json[Constants.KeyParam], this.result);
        }
    }
}

if (typeof module !== "undefined") module.exports = ObjectDecoder;