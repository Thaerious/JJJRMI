"use strict";
class Foo {
	constructor(x) {
		this.x = 0;
		this.x = x;
	}
	static __isTransient() {
		return false;
	}
	static __getClass() {
		return "ca.frar.jjjrmi.test.testable.Foo";
	}
	static __isEnum() {
		return false;
	}
};

if (typeof module !== "undefined") module.exports = Foo;