"use strict";
const NonJJJ = require("./NonJJJ");
class UnknownInternal {
	constructor() {
            this.__init();
            this.nonjjj = new NonJJJ();
        }
	static __isTransient() {
            return true;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.UnknownInternal";
        }
	static __isEnum() {
            return false;
        }
	__init() {
            this.nonjjj = undefined;
        }
};

if (typeof module !== "undefined") module.exports = UnknownInternal;
// Generated by JJJRMI 0.6.3
// 20.02.18 15:08:31
