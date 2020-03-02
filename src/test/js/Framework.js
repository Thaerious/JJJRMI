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
        this.count = 0;
        this.failed = [];
    }
    start(list){
        if (list.length === 0){
            this.allTests();
        } else {
            while (list.length !== 0){
                this.doTest(list.shift());
            }
        }
    }
    run(method) {
        if (method) {
            this.doTest(method);
        } else {
            this.allTests();
        }
    }
    allTests() {
        let methods = getAllMethodNames(this);
        for (let method of methods) {
            if (!method.startsWith("test_")) continue;
            this.count++;
            this.doTest(method);
        }
        console.log(`${this.constructor.name} run: ${this.count} failed: ${this.failed.length}`);
    }
    doTest(method) {
        try {
            this[method]();
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