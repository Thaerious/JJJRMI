"use strict";
class TransientField {
	constructor() {
            this.__init();
            this.x = 5;
            this.y = 5;
        }
	getTransientField() {
            return this.x;
        }
	set(x) {
            this.x = x;
            this.y = x;
            return this;
        }
	static __isRetained() {
            return true;
        }
	__init() {
            this.y = undefined;
        }
	static __isEnum() {
            return false;
        }
	getNonTransientField() {
            return this.y;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.TransientField";
        }
	static __isHandler() {
            return false;
        }
};

if (typeof module !== "undefined") module.exports = TransientField;
// Generated by JJJRMI 0.7.0
// 20.04.02 15:24:11
