"use strict";
class Alphabet {
	constructor(value) {
            this.__value = value;
        }
	static __isRetained() {
            return true;
        }
	static __isTransient() {
            return true;
        }
	static __isEnum() {
            return true;
        }
	toString() {
            return this.__value;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.Alphabet";
        }
	static __isHandler() {
            return false;
        }
};
Alphabet.valueArray = [];
Alphabet.valueArray.push(Alphabet.ALPHA = new Alphabet("ALPHA"));
Alphabet.valueArray.push(Alphabet.BETA = new Alphabet("BETA"));
Alphabet.valueArray.push(Alphabet.CHARLIE = new Alphabet("CHARLIE"));
Alphabet.valueArray.push(Alphabet.DELTA = new Alphabet("DELTA"));
Alphabet.values = function(){return Alphabet.valueArray;};

if (typeof module !== "undefined") module.exports = Alphabet;
// Generated by JJJRMI 0.7.0
// 20.04.02 15:24:12
