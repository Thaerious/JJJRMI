"use strict";
class CircularRef {
	constructor(target = new CircularRef(this)) {
            this.__init();
            this.target = target;
        }
	static __isTransient() {
            return false;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.CircularRef";
        }
	static __isEnum() {
            return false;
        }
	__init() {
            this.target = undefined;
        }
};

if (typeof module !== "undefined") module.exports = CircularRef;
// Generated by JJJRMI 0.6.3
// 20.02.25 14:58:23
