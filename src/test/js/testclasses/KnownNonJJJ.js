"use strict";
const ArrayList = require("JJJRMI-extension").HashMap;
const ArrayList = require("./ArrayList");
class KnownNonJJJ {
	constructor() {
            this.array = new ArrayList();
        }
	static __isTransient() {
            return true;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.KnownNonJJJ";
        }
	static __isEnum() {
            return false;
        }
};

if (typeof module !== "undefined") module.exports = KnownNonJJJ;
// Generated by JJJRMI 0.6.3
// 20.02.17 17:01:26
