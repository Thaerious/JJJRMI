"use strict";
class SimpleConst {
	constructor() {
            this.x = 5;
            this.y = 7;
            this.shape = Shapes.CIRCLE;
            
        }
	static __isTransient() {
            return true;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.SimpleConst";
        }
	static __isEnum() {
            return false;
        }
};

if (typeof module !== "undefined") module.exports = SimpleConst;
// Generated by JJJRMI 0.6.3
// 20.02.17 16:27:27