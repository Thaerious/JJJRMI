"use strict";
class Constants {
	constructor() {
            this.__init();
        }
	static __isTransient() {
            return false;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.Constants";
        }
	static __isEnum() {
            return false;
        }
	__init() {}
};
Constants.GLOBAL = "ima global";

if (typeof module !== "undefined") module.exports = Constants;
// Generated by JJJRMI 0.6.3
// 20.02.25 14:58:23
