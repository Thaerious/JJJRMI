"use strict";
const MyEnum = require("./MyEnum");
const NonEmptyConstructor = require("./NonEmptyConstructor");
class NonEmptySuper extends NonEmptyConstructor {
	constructor() {
            super();
            this.__init();
        }
	static __isTransient() {
            return true;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.NonEmptySuper";
        }
	static __isEnum() {
            return false;
        }
	__init() {}
};

if (typeof module !== "undefined") module.exports = NonEmptySuper;
// Generated by JJJRMI 0.6.3
// 20.02.19 13:19:28
