"use strict";
const None = require("./None");
class HasSuper extends None {
	constructor() {
            super();
            
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
};

if (typeof module !== "undefined") module.exports = HasSuper;
// Generated by JJJRMI 0.6.3
// 20.02.11 14:18:24
