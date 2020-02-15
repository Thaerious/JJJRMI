"use strict";
const Constants = require("./Constants");
class ArrayDecoder {
	constructor(json, translator, componentClass) {
            if (json === null)throw new Error("java.lang.NullPointerException");            
            if (translator === null)throw new Error("java.lang.NullPointerException");
            
            this.json = json;
            this.translator = translator;
            this.elements = json.getJSONArray(Constants.ElementsParam);
            this.componentClass = this.translateComponentClass(componentClass);
        }
        
	decode() {
            let jsonArray = this.json[Constants.ElementsParam];
            let dims = new Array(1);
            dims[0] = jsonArray.length();
            this.result = Array.newInstance(this.componentClass, dims);
            this.translator.addTempReference(this.json.get(Constants.KeyParam).toString(), this.result);
            for(let i = 0; i < this.elements.length(); i++){
                let element = this.elements.getJSONObject(i);
                let decoded = new Decoder(element, this.translator, this.componentClass).decode();
                Array.set(this.result, i, decoded);
            }
            return this.result;
        }
};
ArrayDecoder.json = null;
ArrayDecoder.componentClass = null;
ArrayDecoder.result = null;
ArrayDecoder.translator = null;
ArrayDecoder.elements = null;

if (typeof module !== "undefined") module.exports = ArrayDecoder;
// Generated by JJJRMI 0.6.3
// 20.02.11 11:57:38