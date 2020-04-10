/* global process */
"use strict";

const TranslatorTest = require("./tests/TranslatorTest");
const DecoderTest = require("./tests/DecoderTest");
const fs = require("fs");

fs.readFile("target/test-data/from-js.json", function (err, data) {
    if (err)console.log(err);
    let json = JSON.parse(data.toString());
    let tests = new DecoderTest(json);
    tests.start();
});

fs.readFile("target/test-data/from-java.json", function (err, data) {
    if (err)console.log(err);
    let json = JSON.parse(data.toString());
    let tests = new DecoderTest(json);
    tests.start();
});

const test = new TranslatorTest();
test.start();