"use strict";
class Has {
	constructor() {}
	static __isTransient() {
            return true;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.Has";
        }
	static __isEnum() {
            return false;
        }
};

if (typeof module !== "undefined") module.exports = Has;
// Generated by JJJRMI 0.6.3
// 20.02.17 17:01:27
