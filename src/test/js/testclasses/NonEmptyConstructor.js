"use strict";
class NonEmptyConstructor {
	constructor(x) {
            this.__init();
            this.x = x;
        }
	static __isTransient() {
            return true;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.NonEmptyConstructor";
        }
	static __isEnum() {
            return false;
        }
	__init() {
            this.x = undefined;
        }
};

if (typeof module !== "undefined") module.exports = NonEmptyConstructor;
// Generated by JJJRMI 0.6.3
// 20.02.18 14:48:26
