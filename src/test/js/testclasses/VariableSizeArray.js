"use strict";
class VariableSizeArray {
	constructor(size) {
            this.__init();
            let array = new Array(size);
        }
	static __isRetained() {
            return true;
        }
	__init() {}
	static __isEnum() {
            return false;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.VariableSizeArray";
        }
	static __isHandler() {
            return false;
        }
};

if (typeof module !== "undefined") module.exports = VariableSizeArray;
// Generated by JJJRMI 0.7.0
// 20.04.02 15:24:11
