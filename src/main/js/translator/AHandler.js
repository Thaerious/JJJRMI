"use strict";
const Constants = require("./Constants");
const Encoder = require("./Encoder").Encoder;
const EncodedObject = require("./Encoder").EncodedObject;
const Decoder = require("./Decoder");

class AHandler {
	constructor(encodedResult) {
            this.encodedResult = encodedResult;
        }
	decodeField(fieldName, type) {
            let jsonField = this.jsonFields[fieldName];
            let translator = this.encodedResult.getTranslator();
            let decoded = new Decoder(jsonField, translator).decode();
            return decoded;
        }
	doDecode(t, json) {
            this.jsonFields = json[Constants.FieldsParam];
            this.decode(t);
            return t;
        }
	doEncode(object) {
            let encodedObject = new EncodedObject(object, this.encodedResult);
            this.jsonFields = encodedObject.json[Constants.FieldsParam];
            this.encode(object);
            return encodedObject;
        }
	encodeField(field, value) {
            let toJSON = new Encoder(value, this.encodedResult).encode();
            this.jsonFields[field] = toJSON;
        }
};

if (typeof module !== "undefined") module.exports = AHandler;