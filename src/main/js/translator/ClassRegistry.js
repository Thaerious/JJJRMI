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
        if (typeof aClass !== "function") throw new Error(`paramater 'class' of method 'registerClass' is '${typeof aClass.__getClass}', expected 'function'`);
        if (typeof aClass.__getClass !== "function") throw new Error(`in Class ${aClass.constructor.name} method __getClass of type ${typeof aClass.__getClass}`);
        
        if (aClass.__isHandler()) return;        
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