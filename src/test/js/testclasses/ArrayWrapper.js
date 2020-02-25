"use strict";
const Shapes = require("./Shapes");
class ArrayWrapper {
	constructor() {
            this.__init();
        }
	static __isTransient() {
            return false;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.ArrayWrapper";
        }
	static __isEnum() {
            return false;
        }
	__init() {
            this.arrayField1 = [1, 1, 2, 3, 5];
            this.arrayField2 = new Array(5);
            this.arrayField3 = [Shapes.CIRCLE, Shapes.SQUARE, Shapes.SQUARE, Shapes.TRIANGLE];
        }
};

if (typeof module !== "undefined") module.exports = ArrayWrapper;
// Generated by JJJRMI 0.6.3
// 20.02.25 14:58:23
