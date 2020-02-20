"use strict";
const Shapes = require("./Shapes");
class Simple {
	constructor() {
            this.__init();
        }
	static __isTransient() {
            return true;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.Simple";
        }
	static __isEnum() {
            return false;
        }
	__init() {
            this.x = 5;
            this.y = 7;
            this.shape = Shapes.CIRCLE;
        }
};

if (typeof module !== "undefined") module.exports = Simple;
// Generated by JJJRMI 0.6.3
// 20.02.20 13:21:27
