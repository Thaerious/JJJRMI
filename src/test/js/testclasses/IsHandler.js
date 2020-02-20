"use strict";
const HasHandler = require("./HasHandler");
const Global = require("./Global");
const AHandler = require("./AHandler");
class IsHandler extends AHandler {
	constructor() {
            super();
            this.__init();
        }
	static __isTransient() {
            return false;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.IsHandler";
        }
	static __isEnum() {
            return false;
        }
	__init() {}
	decode(hasHandler) {
            this.decodeField("a", "x");
            this.decodeField("b", "y");
            hasHandler.z = hasHandler.x + hasHandler.y;
            Global.LOGGER.debug(hasHandler.x + ", " + hasHandler.y + ", " + hasHandler.z);
        }
	encode(object) {
            this.encodeField("a", object.x);
            this.encodeField("b", object.y);
        }
	getInstance() {
            return new HasHandler();
        }
};

if (typeof module !== "undefined") module.exports = IsHandler;
// Generated by JJJRMI 0.6.3
// 20.02.20 13:21:28
