/* global process */
"use strict";
const Net = require("net");

const Translator = require("../../main/js/translator/Translator");
const readline = require("readline");

class RemoteTranslator {
    constructor() {
        this.translator = new Translator();
        this.translator.registerPackage(require("./testclasses/packageFile"));
        this.stack = [];
    }

    get lastObject(){
        if (this.stack.length === 0) return undefined;
        return this.stack[0];
    }
    
    set lastObject(object){
        this.stack.unshift(object);
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
     * Decode jsonString, set the result to the last object.
     * @param {string} jsonString, the encoding to decode.
     * @returns String encoding.
     */
    decode(jsonString) {
        let translatorResult = this.translator.decode(jsonString);
        this.lastObject = translatorResult.getRoot();
        console.log(this.lastObject);
        return this.lastObject.constructor.__getClass();
    }

    /**
     * Return the value in field 'name' from the last object.  If the field is
     * not found or there is no last object, return undefined.
     * @param {type} name
     * @returns {undefined}
     */
    getField(name){
        if (this.lastObject) return this.lastObject[name];
        return undefined;
    }

    /**
     * Retrieve the value for field 'name'.  Push the value onto the last object
     * stack and return it.
     * @param {type} name
     * @returns {undefined}
     */
    pushField(name){
        let value = this.getField(name);
        this.lastObject = value;
        return value;
    }

    /**
     * Remove and return the top value on the last object stack.
     * @returns {undefined}
     */
    pop(){
       let value = this.stack.shift();
       return value;
    }

    /**
     * Return true if the last object has a field 'name'.
     * @param {type} name
     * @returns {undefined}
     */
    hasField(name){
        return name in this.lastObject;
    }
    
    /**
     * Return true if the last object is tracked by the translator.
     * @returns {undefined}
     */
    isTracked(){
        return this.translator.hasReferredObject(this.lastObject);
    }

    /*
     * Clear the last object stack;
     * @returns {unresolved}
     */
    clearLast(){
        this.stack = [];
    }

    /**
     * Return the number of objects tracked by the translator.
     * @returns {unresolved}
     */
    size(){
        return this.translator.size();
    }
}

const test = new RemoteTranslator();

let server = Net.createServer(function (socket) {
    console.log("client connected");

    socket.on('end', () => {
        console.log('client disconnected');
    });

    socket.on('data', (data) => {
        let cmd = "", arg = "";
        
        let line = data.toString();
        
        if (line.indexOf(" ") !== -1){
            cmd = line.substring(0, line.indexOf(" ")).trim();
            arg = line.substring(line.indexOf(" ")).trim();
        } else {
            cmd = line.trim();
        }

        console.log("cmd> " + cmd + " " + arg);

        if (!test[cmd]) {
            console.log("command not found: " + cmd);
            socket.write("\n");
        } else {
            let r = test[cmd].call(test, arg);
            console.log("ret> " + r);
            socket.write(r + "\n");
        }
    });
});

server.listen(1337, '127.0.0.1');