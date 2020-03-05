"use strict";
const LOGGER = require("./Logger");
const WebSocket = require('ws');
const Translator = require("./Translator");
const jjjPackage = require("../generated/packageFile");
const JJJMessageType = jjjPackage.JJJMessageType;
const MethodRequest = jjjPackage.MethodRequest;

class JJJRMISocket {
    constructor(socketName) {
        this.jjjSocketName = socketName;
        this.translator = new Translator();
        this.callback = {};
        this.socket = null;
        this.translator.addReferenceListener(obj => obj.__jjjWebsocket = this);
        this.jjjEncode = null;

        this.translator.registerPackage(jjjPackage);
    }
    registerPackage(pkg) {
        this.translator.registerPackage(pkg);
    }
    async connect(url) {
        LOGGER.log("connect", `${this.jjjSocketName} connecting`);
        let cb = function (resolve, reject) {
            this.socket = new WebSocket(url);
            this.onready = resolve;
            this.onreject = reject;

            this.socket.on("message", (data) => this.onMessage(data)); // This line is different for web based js

            this.nextUID = 0;
            this.callback = {};
        }.bind(this);

        return new Promise(cb);
    }
    
    close(){
        this.socket.close();
    }
        
    /**
     * All received messages are parsed by this method.  All messages must of the java type 'RMIResponse' which will
     * always contain the field 'type:RMIResponseType'.
     * @param {type} evt
     * @returns {undefined}
     */
    onMessage(data) {
        LOGGER.verbose("received", JSON.stringify(JSON.parse(data), null, 2));

        /* the main translation from json to js is triggered here */
        let rmiMessage = this.translator.decode(data).getRoot();
        LOGGER.log("received", rmiMessage);

        switch (rmiMessage.type) {
            case JJJMessageType.READY:
            {
                LOGGER.log("onmessage", this.jjjSocketName + " READY");
                LOGGER.log("connect", this.jjjSocketName + " READY");
                this.onready(rmiMessage.getRoot());
                break;
            }
            /* client originated request */
            case JJJMessageType.LOCAL:
            {
                LOGGER.log("onmessage", `Response to client side request: ${this.jjjSocketName} ${rmiMessage.methodName}`);
                let callback = this.callback[rmiMessage.uid];
                delete(this.callback[rmiMessage.uid]);
                callback.resolve(rmiMessage.rvalue);
                break;
            }
            /* server originated request */
            case JJJMessageType.REMOTE:
            {
                LOGGER.log("onmessage", `Server side originated request: ${this.jjjSocketName} ${rmiMessage.methodName}`);
                let target = this.translator.getReferredObject(rmiMessage.ptr);
                this.remoteMethodCallback(target, rmiMessage.methodName, rmiMessage.args);
                break;
            }
            case JJJMessageType.EXCEPTION:
            {
                LOGGER.log("exception", `${this.jjjSocketName} EXCEPTION ${rmiMessage.methodName}`);
                LOGGER.log("exception", `rmiMessage`);
                let callback = this.callback[rmiMessage.uid];
                delete(this.callback[rmiMessage.uid]);
                callback.reject(rmiMessage);
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
            if (!JJJRMISocket.silent) LOGGER.warn(this.socket.url + ":" + target.constructor.name + " does not have remotely invokable method '" + methodName + "'.");
        } else {
            return target[methodName].apply(target, args);
        }
    }
    
    /**
     * Send a remote method invocation to the server.
     * See case JJJMessageType.LOCAL for the handling of the response.
     * @param {type} src the target object
     * @param {type} methodName the method name
     * @param {type} args zero or more method arguments
     * @returns {undefined}
     */
    methodRequest(src, methodName, args) {
        /* method requests come after an object has been sent, this is a sanity check */
        if (!this.translator.hasReferredObject(src)) {
            LOGGER.warn("see window.debug for source");
            window.debug = src;
            throw new Error(`Attempting to call server side method on non-received object: ${src.constructor.name}.${methodName}`);
        }
        
        let uid = this.nextUID++;
        let ptr = this.translator.getReference(src);

        let argsArray = [];
        for (let i in args) argsArray.push(args[i]);

        /* promise function */
        let f = function (resolve, reject) {
            this.callback[uid] = {
                resolve: resolve,
                reject: reject
            };
            
            let packet = new MethodRequest(uid, ptr, methodName, argsArray);
            let translatorResult = this.translator.encode(packet);
            LOGGER.verbose("sent", translatorResult.toString());

            if (this.socket !== null) this.socket.send(translatorResult.toString());
            else Logger.warn(`Socket "${this.socketName}" not connected.`);
        }.bind(this);

        return new Promise(f);
    }    
}

module.exports = JJJRMISocket;