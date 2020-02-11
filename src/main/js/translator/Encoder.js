"use strict";

const EncodedResult = require("./EncodedResult");
const HandlerFactory = require("./HandlerFactory");

class Encoder {
    constructor(object, encodedResult) {
        this.object = object;
        this.encodedResult = encodedResult;
    }
    encode() {
        if (this.object === null) {
            return new EncodedNull();
        } else if (typeof this.object === "number" || typeof this.object === "string" || typeof this.object === "boolean") {
            return new EncodedPrimitive(this.object);
        } else if (this.encodedResult.getTranslator().hasReferredObject(this.object)) {
            return new EncodedReference(this.encodedResult.getTranslator().getReference(this.object));
        } else if (this.object.constructor.__getClass().isArray()) {
            return new EncodedArray(this.object, this.encodedResult);
        } else if (this.object.constructor.__getClass().isEnum()) {
            return new EncodedEnum(this.object);
        } else if (HandlerFactory.getInstance().hasHandler(this.object.constructor.__getClass())) {
            let handlerClass = HandlerFactory.getInstance().getHandler(this.object.constructor.__getClass());
            let handler = handlerClass.getConstructor(EncodedResult.class).newInstance(this.encodedResult);
            let encodedObject = handler.doEncode(this.object);
            this.encodedResult.put(encodedObject);
            return new EncodedReference(this.encodedResult.getTranslator().getReference(this.object));
        } else {
            let encodedObject = new EncodedObject(this.object, this.encodedResult);
            this.encodedResult.put(encodedObject);
            encodedObject.encode();
            return new EncodedReference(this.encodedResult.getTranslator().getReference(this.object));
        }
    }
}

if (typeof module !== "undefined") module.exports = Encoder;