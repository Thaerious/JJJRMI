"use strict";
class HasStatic {
	constructor() {
            this.iAmNotStatic = 6;
            
        }
	static __isTransient() {
            return false;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.HasStatic";
        }
	static __isEnum() {
            return false;
        }
	static iAmMethod() {}
};
HasStatic.iAmInteger = 5;

if (typeof module !== "undefined") module.exports = HasStatic;
// Generated by JJJRMI 0.6.3
// 20.02.11 14:18:24
