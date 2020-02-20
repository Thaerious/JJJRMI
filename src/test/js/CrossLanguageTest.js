/* global process */
"use strict";
const Translator = require("../../main/js/translator/Translator");
const None = require("./testclasses/None");
const HasNone = require("./testclasses/HasNone");
const Primitives = require("./testclasses/Primitives");
const CircularRef = require("./testclasses/CircularRef");
const Simple = require("./testclasses/Simple");
const ArrayWrapper = require("./testclasses/ArrayWrapper");
const PrimitivesExtended = require("./testclasses/PrimitivesExtended");
const Has = require("./testclasses/Has");
const readline = require("readline");


function log(string){
    console.log(string);
    console.log(String.fromCharCode(3));
}

class CrossLanguageTest {
    constructor(){
        this.translator = new Translator();
    }
    
    resend(){
        let encoded = this.translator.encode(this.lastObject);
        log(encoded.toString(2));
    }
    
    get_primitives_extended() {        
        this.lastObject = new PrimitivesExtended(65);
        let encoded = this.translator.encode(this.lastObject);
        log(encoded.toString(2));
    }
    get_array_field() {
        this.lastObject = new ArrayWrapper();
        let encoded = this.translator.encode(this.lastObject);
        log(encoded.toString(2));
    }    
    get_simple() {
        this.lastObject = new Simple();
        let encoded = this.translator.encode(this.lastObject);
        log(encoded.toString(2));
    }    
    get_array_wrapper() {
        this.lastObject = new ArrayWrapper();
        let encoded = this.translator.encode(this.lastObject);
        log(encoded.toString(2));
    }   
    get_none() {
        this.lastObject = new None();
        let encoded = this.translator.encode(this.lastObject);
        log(encoded.toString(2));
    }
    get_has_none() {
        this.lastObject = new HasNone();
        let encoded = this.translator.encode(this.lastObject);
        log(encoded.toString(2));
    }
    get_primitives() {
        this.lastObject = new Primitives();
        let encoded = this.translator.encode(this.lastObject);
        log(encoded.toString(2));
    }
    get_circular(){
        this.lastObject = new CircularRef();
        let encoded = this.translator.encode(this.lastObject);
        log(encoded.toString(2));
    }
    get_has_null(){
        this.lastObject = new Has();
        this.lastObject.set(null);
        let encoded = this.translator.encode(this.lastObject);
        log(encoded.toString(2));
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
        line = line.toLowerCase();
        if (line === "exit") process.exit();
        line = line.replace(/ /g, "_");
        if (!test[line]){
            console.log("unknown command: " + line);
        } else {
            test[line]();        
        }
    });
} else {
    test[process.argv[2]]();
}
