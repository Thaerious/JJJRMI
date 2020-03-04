"use strict";
const ClientMessage = require("./ClientMessage");
const ClientMessageType = require("./ClientMessageType");
class MethodRequest extends ClientMessage {
	constructor(uid, ptr, methodName, args) {
            super(ClientMessageType.METHOD_REQUEST);
            this.__init();
            this.uid = uid;
            this.objectPTR = ptr;
            this.methodName = methodName;
            this.methodArguments = args;
        }
	static __isRetained() {
            return false;
        }
	__init() {
            this.uid = undefined;
            this.objectPTR = undefined;
            this.methodName = undefined;
            this.methodArguments = undefined;
        }
	static __isEnum() {
            return false;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.rmi.MethodRequest";
        }
	static __isHandler() {
            return false;
        }
};

if (typeof module !== "undefined") module.exports = MethodRequest;
// Generated by JJJRMI 0.6.3
// 20.03.04 15:15:05
