"use strict";
const Constants = require("./Constants");
class EncodedEnum {
    constructor(value) {
        this[Constants.EnumParam] = value.constructor.__getClass();
        this[Constants.ValueParam] = value.toString();
    }
}

if (typeof module !== "undefined") module.exports = EncodedEnum;