"use strict";
class HasHandler {
	constructor() {
            this.__init();
        }
	static __isTransient() {
            return true;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.HasHandler";
        }
	static __isEnum() {
            return false;
        }
	__init() {
            this.x = undefined;
            this.y = undefined;
            this.z = undefined;
        }
};

if (typeof module !== "undefined") module.exports = HasHandler;
// Generated by JJJRMI 0.6.3
// 20.02.20 13:21:28
