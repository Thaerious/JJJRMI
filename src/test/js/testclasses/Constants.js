"use strict";
class Constants {
	constructor() {}
	static __isTransient() {
            return false;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.Constants";
        }
	static __isEnum() {
            return false;
        }
};
Constants.GLOBAL = "ima global";

if (typeof module !== "undefined") module.exports = Constants;
// Generated by JJJRMI 0.6.3
// 20.02.17 17:24:26
