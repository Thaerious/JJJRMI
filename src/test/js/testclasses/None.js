"use strict";
class None {
	constructor() {
            this.__init();
        }
	static __isTransient() {
            return true;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.None";
        }
	static __isEnum() {
            return false;
        }
	__init() {}
};

if (typeof module !== "undefined") module.exports = None;
// Generated by JJJRMI 0.6.3
// 20.02.21 16:37:39
