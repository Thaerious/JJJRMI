"use strict";
class JSLambdaCode {
	constructor() {}
	static __isTransient() {
            return true;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.JSLambdaCode";
        }
	static __isEnum() {
            return false;
        }
	callsLambda1() {
            this.takesLambda((e)=>e.toString());
        }
	callsLambda2() {
            this.takesLambda((e)=>{
                e = e.concat(" i really am");
                e.toString();
            });
        }
	takesLambda(consumer) {
            consumer.accept("ima string");
        }
};

if (typeof module !== "undefined") module.exports = JSLambdaCode;
// Generated by JJJRMI 0.6.3
// 20.02.17 17:01:26
