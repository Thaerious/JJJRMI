"use strict"
const ClassRegistry = require("./ClassRegistry");
const LOGGER = require("./Logger");

class HandlerRegistry extends ClassRegistry {
    constructor() {
        super();
    }

    registerClass(aClass) {
        if (!aClass.__isHandler()) return;
        this.classmap.set(aClass.__getHandles(), aClass);
        LOGGER.log("onregister", "Handler Registered: " + aClass.__getHandles());
    }
}

module.exports = HandlerRegistry;