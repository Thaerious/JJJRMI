"use strict";
class EmptyConstructor {
	constructor() {
            this.__init();
        }
	static __isTransient() {
            return true;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.EmptyConstructor";
        }
	static __isEnum() {
            return false;
        }
	__init() {}
};

if (typeof module !== "undefined") module.exports = EmptyConstructor;
// Generated by JJJRMI 0.6.3
// 20.02.19 10:31:26
