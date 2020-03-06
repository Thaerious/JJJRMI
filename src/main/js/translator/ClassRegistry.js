"use strict";
const LOGGER = require("./Logger");

class ClassRegistry {
    constructor() {
        this.classmap = new Map();
    }
    
    /**
     * Register all classes as produced by JJJ in 'packageFile.js'.
     * @param {type} pkg
     * @returns {undefined}
     */
    registerPackage(pkg) {
        LOGGER.log("onregister+", "Class Registry Loading Package");
        for (let aClass in pkg) this.registerClass(pkg[aClass]);
    }

    registerClass(aClass) {
        if (aClass.__isHandler()) return; 
        if (typeof aClass.__getClass !== "function") return;       
        this.classmap.set(aClass.__getClass(), aClass);
        LOGGER.log("onregister", "Class Registered: " + aClass.__getClass());
    }

    hasClass(classname){
        return this.classmap.has(classname);
    }

    getClass(classname) {
        if (!this.classmap.has(classname)) return null;
        return this.classmap.get(classname);
    }
}

module.exports = ClassRegistry;