"use strict";
const Constants = require("./Constants");
class EncodedNull {
    constructor() {
        this[Constants.TypeParam] = Constants.NullValue;
    }
}

if (typeof module !== "undefined") module.exports = EncodedNull;