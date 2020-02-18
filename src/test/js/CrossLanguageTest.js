/* global process */
"use strict";
const Translator = require("../../main/js/translator/Translator");
const None = require("./testclasses/None");
const HasNone = require("./testclasses/HasNone");
const Primitives = require("./testclasses/Primitives");
const CircularRef = require("./testclasses/CircularRef");
const Simple = require("./testclasses/Simple");
const ArrayWrapper = require("./testclasses/ArrayWrapper");
const readline = require("readline");

class CrossLanguageTest {
    get_simple() {
        let translator = new Translator();
        let object = new Simple();
        let encoded = translator.encode(object);
        console.log(encoded.toString(2));
    }    
    get_array_wrapper() {
        let translator = new Translator();
        let object = new ArrayWrapper();
        let encoded = translator.encode(object);
        console.log(encoded);
        console.log(encoded.toString(2));
    }   
    get_none() {
        let translator = new Translator();
        let object = new None();
        let encoded = translator.encode(object);
        console.log(encoded.toString(2));
    }
    get_has_none() {
        let translator = new Translator();
        let object = new HasNone();
        let encoded = translator.encode(object);
        console.log(encoded.toString(2));
    }
    get_primitives() {
        let translator = new Translator();
        let object = new Primitives();
        let encoded = translator.encode(object);
        console.log(encoded.toString(2));
    }
    get_circular(){
        let translator = new Translator();
        let object = new CircularRef();
        let encoded = translator.encode(object);
        console.log(encoded.toString(2));
    }
}

const test = new CrossLanguageTest();

if (process.argv.length === 2) {
    let rl = readline.createInterface({
        input: process.stdin,
        output: process.stdout,
        terminal: false
    });

    rl.on('line', function (line) {
        if (!test[line]) throw "Test not found: " + line;
        test[line]();
        process.exit();
    });
} else {
    test[process.argv[2]]();
}
