"use strict";
const MyEnum = require("./MyEnum");
class ReferenceEnum {
	constructor() {
            this.myEnum = MyEnum.A;
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
	test(myEnum) {
            if (this.getMyEnum() === MyEnum.B)return true;
            
            return false;
        }
};

if (typeof module !== "undefined") module.exports = ReferenceEnum;
// Generated by JJJRMI 0.6.3
// 20.02.17 17:01:26
