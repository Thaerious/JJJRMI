"use strict";
class HasHandler {
	constructor() {}
	static __isTransient() {
            return true;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.HasHandler";
        }
	static __isEnum() {
            return false;
        }
};

if (typeof module !== "undefined") module.exports = HasHandler;
// Generated by JJJRMI 0.6.3
// 20.02.17 17:24:26
