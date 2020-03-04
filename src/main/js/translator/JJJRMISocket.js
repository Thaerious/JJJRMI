"use strict";
const WebSocket = require('ws');
const Translator = require("./Translator");
const jjjPackage = require("../generated/packageFile");
const JJJMessageType = jjjPackage.JJJMessageType;

class JJJRMISocket {
    constructor(socketName) {
        this.jjjSocketName = socketName;
        this.translator = new Translator();
        this.callback = {};
        this.socket = null;
        this.translator.addEncodeListener(obj => obj.__jjjWebsocket = this);
        this.jjjEncode = null;

        this.translator.registerPackage(jjjPackage);
    }
    registerPackage(pkg) {
        this.translator.registerPackage(pkg);
    }
    async connect(url) {
        console.log("connect", `${this.jjjSocketName} connecting`);
        let cb = function (resolve, reject) {
            this.socket = new WebSocket(url);
            this.onready = resolve;
            this.onreject = reject;

            this.socket.on("message", (data) => this.onMessage(data));

            this.nextUID = 0;
            this.callback = {};
        }.bind(this);

        return new Promise(cb);
    }
    /**
     * All received messages are parsed by this method.  All messages must of the java type 'RMIResponse' which will
     * always contain the field 'type:RMIResponseType'.
     * @param {type} evt
     * @returns {undefined}
     */
    onMessage(data) {
        console.log("received", JSON.stringify(JSON.parse(data), null, 2));

        /* the main translation from json to js is triggered here */
        let rmiMessage = this.translator.decode(data);
        console.log("received", rmiMessage);

        switch (rmiMessage.type) {
            case JJJMessageType.FORGET:
            {
                console.log("onmessage", this.jjjSocketName + " FORGET");
                this.translator.removeByKey(rmiMessage.key);
                break;
            }
            case JJJMessageType.READY:
            {
                console.log("onmessage", this.jjjSocketName + " READY");
                console.log("connect", this.jjjSocketName + " READY");
                this.onready(rmiMessage.getRoot());
                break;
            }
            /* client originated request */
            case JJJMessageType.LOCAL:
            {
                console.log("onmessage", `Response to client side request: ${this.jjjSocketName} ${rmiMessage.methodName}`);
                let callback = this.callback[rmiMessage.uid];
                delete(this.callback[rmiMessage.uid]);
                callback.resolve(rmiMessage.rvalue);
                break;
            }
            /* server originated request */
            case JJJMessageType.REMOTE:
            {
                console.log("onmessage", `Server side originated request: ${this.jjjSocketName} ${rmiMessage.methodName}`);
                let target = this.translator.getReferredObject(rmiMessage.ptr);
                this.remoteMethodCallback(target, rmiMessage.methodName, rmiMessage.args);
                break;
            }
            case JJJMessageType.EXCEPTION:
            {
                console.log("exception", `${this.jjjSocketName} EXCEPTION ${rmiMessage.methodName}`);
                console.log("exception", `rmiMessage`);
                let callback = this.callback[rmiMessage.uid];
                delete(this.callback[rmiMessage.uid]);
                callback.reject(rmiMessage);
                break;
            }
            case JJJMessageType.REJECTED_CONNECTION:
            {
                console.log("connect", this.jjjSocketName + " REJECTED_CONNECTION");
                console.log("onmessage", this.jjjSocketName + " REJECTED_CONNECTION");
                this.onreject();
                break;
            }
        }
    }
    /**
     * Handles a server originated request.  Will throw a warning if the client does not have a method to handle the
     * request.
     * @param {type} target
     * @param {type} methodName
     * @param {type} args
     * @returns {undefined}
     */
    remoteMethodCallback(target, methodName, args) {
        if (typeof target[methodName] !== "function") {
            if (!JJJRMISocket.silent) console.warn(this.socket.url + ":" + target.constructor.name + " does not have remotely invokable method '" + methodName + "'.");
        } else {
            return target[methodName].apply(target, args);
        }
    }
}

module.exports = JJJRMISocket;