"use strict";
class Foo {
	constructor(x) {
            this.__init();
            this.x = x;
        }
	static __isTransient() {
            return true;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.Foo";
        }
	static __isEnum() {
            return false;
        }
	__init() {
            this.x = 0;
        }
};

if (typeof module !== "undefined") module.exports = Foo;
// Generated by JJJRMI 0.6.3
// 20.02.19 10:31:26
