"use strict";
const ArrayList = require("./ArrayList");
class UnknownNonJJJ {
	constructor() {
            this.__init();
            this.array = new ArrayList();
        }
	static __isTransient() {
            return true;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.UnknownNonJJJ";
        }
	static __isEnum() {
            return false;
        }
	__init() {
            this.array = undefined;
        }
};

if (typeof module !== "undefined") module.exports = UnknownNonJJJ;
// Generated by JJJRMI 0.6.3
// 20.02.19 10:31:25
