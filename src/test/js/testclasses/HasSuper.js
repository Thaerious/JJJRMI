"use strict";
const None = require("./None");
class HasSuper extends None {
	constructor() {
            super();
            this.__init();
        }
	static __isTransient() {
            return false;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.HasSuper";
        }
	static __isEnum() {
            return false;
        }
	__init() {}
};

if (typeof module !== "undefined") module.exports = HasSuper;
// Generated by JJJRMI 0.6.3
// 20.02.22 21:46:13
