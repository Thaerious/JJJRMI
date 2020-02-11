"use strict";

class HandlerFactory {
    hasHandler() {
        return false;
    }
    static getInstance() {
        if (!HandlerFactory.instance) {
            HandlerFactory.instance = new HandlerFactory();
        }
        return HandlerFactory.instance;
    }
}

module.exports = HandlerFactory;