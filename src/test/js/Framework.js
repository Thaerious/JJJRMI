/* global process, Reflect */
"use strict";

function getAllMethodNames(obj) {
    let methods = new Set();
    while (obj) {
        let keys = Reflect.ownKeys(obj);
        keys.forEach((k) => methods.add(k));
        obj = Reflect.getPrototypeOf(obj);
    }
    return methods;
}

class AssertError {
    constructor(message) {
        this.message = message;
    }
}

class Assert {
    static equals(expected, found) {
        if (expected !== found) {
            throw new AssertError(`expected "${expected}" found "${found}"`);
        }
    }
    
    static notEquals(expected, found) {
        if (expected === found) {
            throw new AssertError(`not equals expected, "${expected}" === "${found}"`);
        }
    }    
    
    static true(found) {
        if (found !== true) {
            throw new AssertError(`expected "true" found "${found}"`);
        }
    }
    
    static false(found) {
        if (found !== false) {
            throw new AssertError(`expected "true" found "${found}"`);
        }
    }    
}

class TestFramework {
    constructor() {
        this.verbose = false;
        this.count = 0;
        this.failed = [];
    }
    
    /**
     * Run the tests listed in 'list'.  If list is undefined run all tests.
     * @param {type} list
     * @returns {undefined}
     */
    async start(list = []){
        if (list.length === 0){
            await this.allTests();
        } else {
            while (list.length !== 0){
                await this.doTest(list.shift());
            }
        }
    }
    async allTests() {
        let methods = getAllMethodNames(this);
        for (let method of methods) {
            if (!method.startsWith("test_")) continue;
            this.count++;
            await this.doTest(method);
        }
        console.log(`${this.constructor.name} - run: ${this.count}, failed: ${this.failed.length}`);
    }
    async doTest(method) {
        try {
            if (this.verbose) console.log("running: " + method);
            await this[method]();
        } catch (err) {
            if (err instanceof AssertError) {
                this.failed.push(method);
                console.log(`${method} failed`);
                console.log(`- ${err.message}\n`);
            } else {
                throw err;
            }
        }
    }
}

module.exports = {
    TestFramework : TestFramework,
    Assert : Assert
};