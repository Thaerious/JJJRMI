"use strict";
class InConstructor {
	constructor() {
            this.__init();
            this.a = 'a';
            this.b = String.fromCharCode(66);
            this.c = String.fromCharCode(66);
            this.d = String.fromCharCode(66);
        }
	static __isTransient() {
            return false;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.charcode.InConstructor";
        }
	static __isEnum() {
            return false;
        }
	__init() {
            this.a = undefined;
            this.b = undefined;
            this.c = undefined;
            this.d = undefined;
        }
};

if (typeof module !== "undefined") module.exports = InConstructor;
// Generated by JJJRMI 0.6.3
// 20.02.19 10:31:26
