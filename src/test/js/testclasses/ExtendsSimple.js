"use strict";
const Simple = require("./Simple");
class ExtendsSimple extends Simple {
	constructor() {
            super();
            this.__init();
        }
	static __isTransient() {
            return true;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.ExtendsSimple";
        }
	static __isEnum() {
            return false;
        }
	__init() {}
};

if (typeof module !== "undefined") module.exports = ExtendsSimple;
// Generated by JJJRMI 0.6.3
// 20.02.20 13:21:28