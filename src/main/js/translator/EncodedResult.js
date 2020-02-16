"use strict";
const Constants = require("./Constants");

class EncodedResult {
    constructor(translator) {
        this.translator = translator;
        this[Constants.NewObjects] = {}
    }
    getAllObjects() {
        let list = [];
        for (let key in this[Constants.NewObjects]) {
            list.push(this[Constants.NewObjects][key]);
        }
        return list;
    }
    getRoot() {
        return this[Constants.RootObject];
    }
    getTranslator() {
        return this.translator;
    }
    put(encodedObject) {
        let key = encodedObject.json[Constants.KeyParam];
        this[Constants.NewObjects][key] = encodedObject;
    }
    setRoot(rootKey) {
        this[Constants.RootObject] = rootKey;
    }    
    toString(indent = 0){
        return JSON.stringify(this, null, indent);
    }
    
    toJSON(){
        return this.json;
    }
}
;

if (typeof module !== "undefined") module.exports = EncodedResult;