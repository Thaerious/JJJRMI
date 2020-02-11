"use strict";
const Constants = require("./Constants");
class EncodedReference {
	constructor(ref) {
            this[Constants.PointerParam] = ref;
        }
};

if (typeof module !== "undefined") module.exports = EncodedReference;