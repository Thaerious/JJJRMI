"use strict";
class Alphabet {
	constructor(value) {
            this.__value = value;
        }
	toString() {
            return this.__value;
        }
	static __isTransient() {
            return true;
        }
	static __getClass() {
            return "ca.frar.jjjrmi.testclasses.Alphabet";
        }
	static __isEnum() {
            return true;
        }
};
Alphabet.valueArray = [];
Alphabet.valueArray.push(Alphabet.ALPHA = new Alphabet("ALPHA"));
Alphabet.valueArray.push(Alphabet.BETA = new Alphabet("BETA"));
Alphabet.valueArray.push(Alphabet.CHARLIE = new Alphabet("CHARLIE"));
Alphabet.valueArray.push(Alphabet.DELTA = new Alphabet("DELTA"));
Alphabet.values = function(){return Alphabet.valueArray;};

if (typeof module !== "undefined") module.exports = Alphabet;
// Generated by JJJRMI 0.6.3
// 20.02.18 15:08:32
