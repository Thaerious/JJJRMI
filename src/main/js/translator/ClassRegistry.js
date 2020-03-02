"use strict"

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
        for (let aClass in pkg) this.registerClass(pkg[aClass]);
    }

    registerClass(aClass) {
        if (aClass.__isHandler()) return; 
        if (typeof aClass.__getClass !== "function") return;       
        this.classmap.set(aClass.__getClass(), aClass);
    }

    hasClass(classname){
        return this.classmap.has(classname);
    }

    getClass(classname) {
        if (!this.classmap.has(classname)) return null;
        return this.classmap.get(classname);
    }

    /**
     * Copy all registered classes from a given map.
     * @param {type} map
     * @returns {undefined}
     */
    copyFrom(map) {
        for (let classname of map.keys()) {
            this.classmap.set(classname, map.get(classname));
        }
    }
}

module.exports = ClassRegistry;