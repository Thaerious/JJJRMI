"use strict";
const ArrayList = require("JJJRMI-extension").HashMap;
const ArrayList = require("./ArrayList");
class KnownNonJJJ {
	constructor() {
            this.__init();
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
	__init() {
            this.array = undefined;
        }
};

if (typeof module !== "undefined") module.exports = KnownNonJJJ;
// Generated by JJJRMI 0.6.3
// 20.02.18 14:48:27
