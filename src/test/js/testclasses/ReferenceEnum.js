"use strict";
const MyEnum = require("./MyEnum");
class ReferenceEnum {
	constructor() {
            this.__init();
        }
	static __isTransient() {
            return true;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.ReferenceEnum";
        }
	static __isEnum() {
            return false;
        }
	__init() {
            this.myEnum = MyEnum.A;
        }
	test(myEnum) {
            if (this.getMyEnum() === MyEnum.B);
            return false;
        }
};

if (typeof module !== "undefined") module.exports = ReferenceEnum;
// Generated by JJJRMI 0.6.3
// 20.02.19 10:31:26
