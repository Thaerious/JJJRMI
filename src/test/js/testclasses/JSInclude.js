"use strict";
const JSLambdaCode = require("./JSLambdaCode");
class JSInclude {
	constructor() {
            this.__init();
        }
	static __isTransient() {
            return true;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.JSInclude";
        }
	static __isEnum() {
            return false;
        }
	__init() {
            this.callback = undefined;
        }
	run() {
            this.callback = new JSLambdaCode();
        }
};

if (typeof module !== "undefined") module.exports = JSInclude;
// Generated by JJJRMI 0.6.3
// 20.02.18 14:48:27
