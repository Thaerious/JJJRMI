"use strict"
const ClassRegistry = require("./ClassRegistry");
const LOGGER = require("./Logger");

class HandlerRegistry extends ClassRegistry {
    constructor() {
        super();
        this.classNames = new Map();  // a map from js class constructors to java class names
    }

    registerClass(aClass) {
        if (!aClass.__isHandler) return;
        if (!aClass.__isHandler()) return;
        this.classmap.set(aClass.__getHandles(), aClass);
        this.classNames.set(new aClass().getInstance().constructor, aClass.__getHandles());
        LOGGER.log("onregister", "Handler Registered: " + aClass.__getHandles());
    }

    /**
     * Retrieve the handler either by java canonical name (string) or js class function.
     * @param classname
     * @returns {null|unknown}
     */
    getClass(aClass) {
        if (typeof aClass == "function") {
            let classname = this.classNames.get(aClass);
            return this.classmap.get(classname);
        }

        if (!this.classmap.has(aClass)) return null;
        return this.classmap.get(aClass);
    }

    /**
     * Poll the availability of a handler either by java canonical name (string) or js class function.
     * @param classname
     * @returns {boolean}
     */
    hasClass(aClass){
        if (typeof aClass == "function") {
            let classname = this.classNames.get(aClass);
            return this.classmap.has(classname);
        }

        return this.classmap.has(aClass);
    }
}

module.exports = HandlerRegistry;