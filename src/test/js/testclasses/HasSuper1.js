"use strict";
const None = require("./None");
class HasSuper1 extends None {
	constructor() {
            super();
            this.__init();
        }
	static __isTransient() {
            return false;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.HasSuper1";
        }
	static __isEnum() {
            return false;
        }
	__init() {}
};

if (typeof module !== "undefined") module.exports = HasSuper1;
// Generated by JJJRMI 0.6.3
// 20.02.18 14:48:27