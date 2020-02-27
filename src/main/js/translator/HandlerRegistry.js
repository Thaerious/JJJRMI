"use strict"
const ClassRegistry = require("./ClassRegistry");

class HandlerRegistry extends ClassRegistry {
    constructor() {
        super();
    }

    registerClass(aClass) {
        if (typeof aClass !== "function") throw new Error(`paramater 'class' of method 'registerClass' is '${typeof aClass.__getClass}', expected 'function'`);
        if (typeof aClass.__getClass !== "function") throw new Error(`in Class ${aClass.constructor.name} method __getClass of type ${typeof aClass.__getClass}`);
        if (!aClass.__isHandler()) return;
        this.classmap.set(aClass.__getHandles(), aClass);
    }
}

module.exports = HandlerRegistry;