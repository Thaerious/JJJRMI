"use strict";
class MyEnum {
	constructor(value) {
            this.__value = value;
        }
	toString() {
            return this.__value;
        }
	static __isTransient() {
            return true;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.MyEnum";
        }
	static __isEnum() {
            return true;
        }
};
MyEnum.valueArray = [];
MyEnum.valueArray.push(MyEnum.A = new MyEnum("A"));
MyEnum.valueArray.push(MyEnum.B = new MyEnum("B"));
MyEnum.valueArray.push(MyEnum.C = new MyEnum("C"));
MyEnum.valueArray.push(MyEnum.D = new MyEnum("D"));
MyEnum.values = function(){return MyEnum.valueArray;};

if (typeof module !== "undefined") module.exports = MyEnum;
// Generated by JJJRMI 0.6.3
// 20.02.14 16:37:19