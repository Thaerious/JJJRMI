/* global process */
"use strict";
const Translator = require("../../main/js/translator/Translator");
const None = require("./testclasses/None");
const HasNone = require("./testclasses/HasNone");
const Primitives = require("./testclasses/Primitives");
const HasHandler = require("./testclasses/HasHandler");
const readline = require("readline");

class TranslatorCorrectnessTest {
    test_has_handler(){
        let translator = new Translator();
        translator.registerPackage(require("./testclasses/packageFile.js"));
        let object = new HasHandler(2, 5);
        let encoded = translator.encode(object);
        console.log(encoded);
        translator.clear();
        let decoded = translator.decode(encoded);
        assertEquals(7, decoded.z);
    }
}
const test = new TranslatorCorrectnessTest();

if (process.argv.length === 2) {
    let rl = readline.createInterface({
        input: process.stdin,
        output: process.stdout,
        terminal: false
    });

    rl.on('line', function (line) {
        test[line]();
        process.exit();
    });
} else {
    test[process.argv[2]]();
}

module.exports = TranslatorCorrectnessTest;