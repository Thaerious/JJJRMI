"use strict";
const Constants = require("./Constants");
class EncodedPrimitive {
    constructor(value) {
        this[Constants.PrimitiveParam] = typeof value;
        this[Constants.ValueParam] = value;
    }
}

if (typeof module !== "undefined") module.exports = EncodedPrimitive;