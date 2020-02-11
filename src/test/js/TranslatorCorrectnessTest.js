/* global process */
"use strict";
const Translator = require("../../main/js/translator/Translator");
const None = require("./testclasses/None");
const readline = require("readline");

class TranslatorCorrectnessTest {
    get_none() {
        let translator = new Translator();
        let object = new None();
        let encoded = translator.encode(object);
        console.log(encoded.toString(2));
    }
}
const test = new TranslatorCorrectnessTest();

const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout,
    terminal: false
});

rl.on('line', function (line) {
    test[line]();
    process.exit();
});

module.exports = TranslatorCorrectnessTest;