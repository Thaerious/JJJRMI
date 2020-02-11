"use strict";
const Constants = require("./Constants");
class EncodedPrimitive {
    constructor(value) {
        this.json[Constants.PrimitiveParam] = typeof value;
        this.json[Constants.ValueParam] = value;
    }
}

if (typeof module !== "undefined") module.exports = EncodedPrimitive;