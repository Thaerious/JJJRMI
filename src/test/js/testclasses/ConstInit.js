"use strict";
const Shapes = require("./Shapes");
class ConstInit {
	constructor() {
            this.__init();
            let x;
            let y;
            let shape = Shapes.CIRCLE;
        }
	static __isTransient() {
            return true;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.ConstInit";
        }
	static __isEnum() {
            return false;
        }
	__init() {
            this.x = undefined;
            this.y = undefined;
            this.shape = undefined;
        }
};

if (typeof module !== "undefined") module.exports = ConstInit;
// Generated by JJJRMI 0.6.3
// 20.02.21 16:37:39
