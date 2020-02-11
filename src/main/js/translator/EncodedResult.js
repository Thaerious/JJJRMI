"use strict";
const Constants = require("./Constants");

class EncodedResult {
    constructor(translator) {
        this.translator = translator;
        this.json = {
            [Constants.NewObjects] : {}
        };
    }
    getAllObjects() {
        let list = [];
        for (let key of this.json[Constants.NewObjects]) {
            list.push(this.objects[key]);
        }
        return list;
    }
    getRoot() {
        return this.json[Constants.RootObject];
    }
    getTranslator() {
        return this.translator;
    }
    put(encodedObject) {
        let key = encodedObject.json[Constants.KeyParam];
        this.json[Constants.NewObjects][key] = encodedObject;
    }
    setRoot(rootKey) {
        this.json[Constants.RootObject] = rootKey;
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