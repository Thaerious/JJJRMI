"use strict";
const ArrayList = require("JJJRMI-extension").HashMap;
const ArrayList = require("./ArrayList");
class KnownNonJJJ {
	constructor() {
            /* no super class */;
            this.array = new ArrayList();
        }
	static __isTransient() {
            return false;
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
// 20.02.11 14:18:24
