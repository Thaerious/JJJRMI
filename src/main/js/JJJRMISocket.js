"use strict";
const Translator = require("./Translator");
const MethodRequest = require("./MethodRequest");
const JJJMessageType = require("./JJJMessageType");
const LOGGER = require("./Logger");

class JJJRMISocket {
    constructor(socketName) {        
        this.jjjSocketName = socketName;
        this.translator = new Translator();
        this.callback = {};
        this.socket = null;
        this.translator.copyFrom(JJJRMISocket.classes);

        this.translator.addDecodeListener(obj => obj.__jjjWebsocket = this);
        this.translator.addEncodeListener(obj => obj.__jjjWebsocket = this);
        this.jjjEncode = null;
    }

    getHandler(aClass) {
        return this.translator.getHandler(aClass);
    }
    hasHandler(aClass) {
        return this.translator.hasHandler(aClass);
    }
    setHandler(aClass, handler) {
        this.translator.setHandler(aClass, handler);
    }

    async connect(url) {
        LOGGER.log("connect", `${this.jjjSocketName} connecting`);
        if (!url) url = this.getAddress();

        let cb = function (resolve, reject) {
            this.socket = new WebSocket(url);
            this.onready = resolve;
            this.onreject = reject;
            this.socket.onerror = (err) => {
                console.error("websocket error");
                console.error(err);
                reject(err);
            };
            this.socket.onmessage = (evt) => this.onMessage(evt);
            this.nextUID = 0;
            this.callback = {};
        }.bind(this);

        return new Promise(cb);
    }

    getAddress() {
        let prequel = "ws://";
        if (window.location.protocol === "https:") prequel = "wss://";
        let pathname = window.location.pathname.substr(1);
        pathname = pathname.substr(0, pathname.indexOf("/"));
        return `${prequel}${window.location.host}/${pathname}/${this.jjjSocketName}`;
    }

    reset() {
        this.translator.clear();
    }

    /**
     * Send a method request to the server.
     * callbacks.
     * @param {type} src
     * @param {type} methodName
     * @param {type} args
     * @returns {undefined}
     */
    methodRequest(src, methodName, args) {
        /* method requests come after an object has been sent, this is a sanity check */
        if (!this.translator.hasReferredObject(src)) {
            console.warn("see window.debug for source");
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
            let encodedPacket = this.translator.encode(packet);
            LOGGER.log("sent", encodedPacket);
            let encodedString = JSON.stringify(encodedPacket, null, 4);
            LOGGER.verbose("sent", encodedString);

            if (this.socket !== null) this.socket.send(encodedString);
            else console.warn(`Socket "${this.socketName}" not connected.`);
        }.bind(this);

        return new Promise(f);
    }
    /**
     * All received messages are parsed by this method.  All messages must of the java type 'RMIResponse' which will
     * always contain the field 'type:RMIResponseType'.
     * @param {type} evt
     * @returns {undefined}
     */
    onMessage(evt) {
        LOGGER.verbose("received", JSON.stringify(JSON.parse(evt.data), null, 2));
        
        /* the main translation from json to js is triggered here */
        let rmiMessage = this.translator.decode(evt.data);
        LOGGER.log("received", rmiMessage);

        switch (rmiMessage.type) {
            case JJJMessageType.FORGET:
            {
                LOGGER.log("onmessage", this.jjjSocketName + " FORGET");
                this.translator.removeByKey(rmiMessage.key);
                break;
            }
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
            case JJJMessageType.REJECTED_CONNECTION:
            {
                LOGGER.log("connect", this.jjjSocketName + " REJECTED_CONNECTION");
                LOGGER.log("onmessage", this.jjjSocketName + " REJECTED_CONNECTION");
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

    /**
     * Performs checks on class before registering it with translator.
     * @param {type} aClass
     * @returns {undefined}
     */
    registerClass(aClass) {
        if (typeof aClass !== "function") {
            LOGGER.log("exception", aClass);
            throw new Error(`paramater 'class' of method 'registerClass' is '${typeof aClass}', expected 'function'`);
        }

        if (typeof aClass.__getClass !== "function") return;
        if (typeof aClass.__isEnum !== "function") return;
        if (typeof aClass.__isTransient !== "function") return;

        LOGGER.log("onregister", `Register ${aClass.__getClass()}`);
        this.translator.registerClass(aClass);

        for (let field in aClass) {
            LOGGER.verbose("onregister", `considering ${aClass.__getClass()}.${field}`);
            if (typeof aClass[field] === "function" && typeof aClass[field].__getClass === "function") {
                this.registerClass(aClass[field]);
            }
        }
    }
    
    /* for registering all classes returned from generated JS */
    registerPackage(packageFile) {
        for (let aClass in packageFile) {
            this.registerClass(packageFile[aClass]);
        }
    }
}
;

JJJRMISocket.classes = new Map();

JJJRMISocket.registerClass = function (aClass) {
    if (typeof aClass !== "function") {
        LOGGER.log("exception", aClass);
        throw new Error(`paramater 'class' of method 'registerClass' is '${typeof aClass}', expected 'function'`);
    }
    
    if (typeof aClass.__getClass !== "function") console.verbose("onregister", `__getClass not of type function`);
    else if (typeof aClass.__isEnum !== "function") console.verbose("onregister", `__isEnum not of type function`);
    else if (typeof aClass.__isTransient !== "function") console.verbose("onregister", `__isTransient not of type function`);

    if (typeof aClass.__getClass !== "function") return;
    if (typeof aClass.__isEnum !== "function") return;
    if (typeof aClass.__isTransient !== "function") return;

    LOGGER.log("onregister", `Register ${aClass.__getClass()}`);

    JJJRMISocket.classes.set(aClass.__getClass(), aClass);

    for (let field in aClass) {
        LOGGER.verbose(`considering ${aClass.__getClass()}.${field}`);
        if (typeof aClass[field] === "function" && typeof aClass[field].__getClass === "function") {
            JJJRMISocket.registerClass(aClass[field]);
        }
    }
};

/* for registering all classes returned from generated JS */
JJJRMISocket.registerPackage = function (packageFile) {
    for (let aClass in packageFile) {
        JJJRMISocket.registerClass(packageFile[aClass]);
    }
};

/* register the classes required for JJJRMISocket */
JJJRMISocket.registerClass(require("./ClientMessage"));
JJJRMISocket.registerClass(require("./ClientMessageType"));
JJJRMISocket.registerClass(require("./ClientRequestMessage"));
JJJRMISocket.registerClass(require("./ForgetMessage"));
JJJRMISocket.registerClass(require("./JJJMessage"));
JJJRMISocket.registerClass(require("./JJJMessageType"));
JJJRMISocket.registerClass(require("./MethodRequest"));
JJJRMISocket.registerClass(require("./MethodResponse"));
JJJRMISocket.registerClass(require("./ReadyMessage"));
JJJRMISocket.registerClass(require("./RejectedConnectionMessage"));
JJJRMISocket.registerClass(require("./ServerSideExceptionMessage"));

JJJRMISocket.Translator = Translator;
module.exports = JJJRMISocket;