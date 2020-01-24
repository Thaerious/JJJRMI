"use strict";
class JSLambdaCode {
	constructor() {
		
	}
	static __isTransient() {
		return false;
	}
	static __getClass() {
		return "ca.frar.jjjrmi.jsbuilder.code.JSLambdaCode";
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