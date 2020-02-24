/* global process */
"use strict";
const Net = require("net");

const Translator = require("../../main/js/translator/Translator");
const readline = require("readline");

class RemoteTranslator {
    constructor() {
        this.translator = new Translator();
        this.translator.classRegistry.registerPackage(require("./testclasses/packageFile"));
    }

    echo(string) {
        return string;
    }

    exit() {
        process.exit();
    }

    error() {
        throw new Error("error");
    }

    /**
     * Re-encdode and send the last encoded object.
     * @returns String encoding.
     */
    resend() {
        let encoded = this.translator.encode(this.lastObject);
        return encoded.toString(2);
    }

    /**
     * Re-encdode and send the last encoded object.
     * @returns String encoding.
     */
    decode(jsonString) {
        let translatorResult = this.translator.decode(jsonString);
        this.lastObject = translatorResult.getRoot();
        console.log(this.lastObject);
        return this.lastObject.constructor.__getClass();
    }

    encode(reference) {
    }
}

const test = new RemoteTranslator();

let server = Net.createServer(function (socket) {
    console.log("client connected");

    socket.on('end', () => {
        console.log('client disconnected');
    });

    socket.on('data', (data) => {
        let line = data.toString();
        let cmd = line.substring(0, line.indexOf(" ")).trim();
        let arg = line.substring(line.indexOf(" ")).trim();

        console.log("cmd> '" + cmd + "'");
        console.log("arg> '" + arg + "'");

        if (!test[cmd]) {
            console.log("command not found: " + cmd);
            socket.write("");
        } else {
            let r = test[cmd].call(test, arg);
            socket.write(r + "\n");
        }
    });
});

server.listen(1337, '127.0.0.1');