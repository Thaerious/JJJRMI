"use strict";
class MissingConstructor {
	constructor() {
        }
	static __isTransient() {
            return true;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.MissingConstructor";
        }
	static __isEnum() {
            return false;
        }
};

if (typeof module !== "undefined") module.exports = MissingConstructor;
// Generated by JJJRMI 0.6.3
// 20.02.14 16:37:19
