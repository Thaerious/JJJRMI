"use strict"
const ClassRegistry = require("./ClassRegistry");

class HandlerRegistry extends ClassRegistry {
    constructor() {
        super();
    }

    registerClass(aClass) {
        if (!aClass.__isHandler()) return;
        this.classmap.set(aClass.__getHandles(), aClass);
    }
}

module.exports = HandlerRegistry;