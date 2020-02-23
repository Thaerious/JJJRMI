/* global process */
"use strict";
const Translator = require("../../main/js/translator/Translator");
const readline = require("readline");

function log(string) {
    if (string) console.log(string);
    console.log(String.fromCharCode(3));
}

class RemoteTranslator {
    constructor() {
        this.translator = new Translator();
        this.translator.classRegistry.registerPackage(require("./testclasses/packageFile"));
    }

    echo(string) {
        log(string);
    }

    exit() {
        process.exit();
    }

    /**
     * Re-encdode and send the last encoded object.
     * @returns String encoding.
     */
    resend() {
        let encoded = this.translator.encode(this.lastObject);
        log(encoded.toString(2));
    }

    /**
     * Re-encdode and send the last encoded object.
     * @returns String encoding.
     */
    decode(jsonString) {
        let translatorResult = this.translator.decode(jsonString);
        this.lastObject = translatorResult.getRoot();
        console.log(this.lastObject);
        return this.lastObject;
    }

    encode(reference) {
    }
}

const test = new RemoteTranslator();

if (process.argv.length === 2) {
    let rl = readline.createInterface({
        input: process.stdin,
        output: process.stdout,
        terminal: false
    });

    rl.on('line', function (line) {
        if (line.indexOf(" ") === -1){
            test[line].call(line);
        }
        else {
            let cmd = line.substring(0, line.indexOf(" ")).trim();
            let arg = line.substring(line.indexOf(" ")).trim();

            if (!test[cmd]) console.log("command not found: " + cmd);
            else test[cmd].call(test, arg);
        }
    });
} else {
    process.argv.shift();
    process.argv.shift();
    let cmd = process.argv.shift();
    let arg = "";
    
    while (process.argv.length > 0){
        arg = arg + process.argv.shift() + " ";
    }
    arg = arg.trim();
    
    console.log("cmd: " + cmd);
    console.log("arg: " + arg);
    
    test[cmd].call(test, arg);
}
