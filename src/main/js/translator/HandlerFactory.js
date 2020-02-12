"use strict";

class HandlerFactory {
    static getInstance() {
        if (!HandlerFactory.instance) {
            HandlerFactory.instance = new HandlerFactory();
        }
        return HandlerFactory.instance;
    }
    
    constructor(){
        this.map = new Map();
    }
    
    /**
     * Associate a handler for a class.
     * @param {type} forClassName The name of the target class file
     * @param {type} handlerClass The handler class file
     * @returns {undefined}
     */
    setHandler(forClassName, handlerClass) {
        this.map.set(forClassName, handlerClass);
    }
    hasHandler(forClassName) {
        return this.map.has(forClassName);
    }
    getHandler(forClassName) {
        return this.map.get(forClassName);
    }
}

module.exports = HandlerFactory;