"use strict";
class ConstInit {
	constructor() {
            let x = 5;
            let y = 7;
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
};

if (typeof module !== "undefined") module.exports = ConstInit;
// Generated by JJJRMI 0.6.3
// 20.02.17 16:27:27
