"use strict";
class Foo {
	constructor(x) {
            this.x = 0;
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
};

if (typeof module !== "undefined") module.exports = Foo;
// Generated by JJJRMI 0.6.3
// 20.02.17 17:01:27
