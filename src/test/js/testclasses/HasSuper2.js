"use strict";
const None = require("./None");
class HasSuper2 extends None {
	constructor() {
            super();
            this.__init();
            this.x = 5;
        }
	static __isTransient() {
            return false;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.HasSuper2";
        }
	static __isEnum() {
            return false;
        }
	__init() {
            this.x = undefined;
        }
};

if (typeof module !== "undefined") module.exports = HasSuper2;
// Generated by JJJRMI 0.6.3
// 20.02.20 13:21:28
