/* global process, Reflect */
"use strict";
const fs = require("fs");
const filename = process.argv[2];
const testname = process.argv[3];
const Translator = require("../../main/js/translator/Translator");
const PackageFile = require("./testclasses/packageFile");

fs.readFile(filename, function (err, data) {
    let json = JSON.parse(data.toString());
    let tests = new Tests(json);
    tests.run(testname);
});

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
}

class Tests {
    constructor(json) {
        this.json = json;
        this.count = 0;
        this.failed = [];
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
        console.log(`run: ${this.count} failed: ${this.failed.length}`);
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
    /**
     * Sanity test to make sure the translator loads and processes.
     */
    test_none() {
        let translator = new Translator();
        translator.classRegistry.registerPackage(PackageFile);
        let text = JSON.stringify(this.json.none);
        let object = translator.decode(text).getRoot();
        Assert.equals("ca.frar.jjjrmi.testclasses.None", object.constructor.__getClass());
    }
    /**
     * A class will be populated with default values.
     */
    test_simple() {
        let translator = new Translator();
        translator.classRegistry.registerPackage(PackageFile);
        let text = JSON.stringify(this.json.simple);
        let object = translator.decode(text).getRoot();
        Assert.equals("ca.frar.jjjrmi.testclasses.Simple", object.constructor.__getClass());
        Assert.equals(5, object.x);
        Assert.equals(7, object.y);
        Assert.equals(PackageFile.Shapes.CIRCLE, object.shape);
    }
    /**
     * The default values of a class will be overwritten by the data.
     */
    test_primitives() {
        let translator = new Translator();
        translator.classRegistry.registerPackage(PackageFile);
        let text = JSON.stringify(this.json.primitives);
        let object = translator.decode(text).getRoot();
        Assert.equals("ca.frar.jjjrmi.testclasses.Primitives", object.constructor.__getClass());
        Assert.equals("alpha9", object.string);
    }
    /**
     * Classes that indirectly extend JJJObject will include exteded fields.
     */
    test_primitivesExtended() {
        let translator = new Translator();
        translator.classRegistry.registerPackage(PackageFile);
        let text = JSON.stringify(this.json.primitivesExtended);
        let object = translator.decode(text).getRoot();
        Assert.equals("ca.frar.jjjrmi.testclasses.PrimitivesExtended", object.constructor.__getClass());
        Assert.equals("alpha16", object.string);
    }
    /**
     * Null fields will be instantiated with null.
     */
    test_hasNull() {
        let translator = new Translator();
        translator.classRegistry.registerPackage(PackageFile);
        let text = JSON.stringify(this.json.hasNull);
        let object = translator.decode(text).getRoot();
        Assert.equals("ca.frar.jjjrmi.testclasses.Has", object.constructor.__getClass());
        Assert.equals(null, object.t);
    }
    /**
     * Cached objects can be retrieved by reference.  This reference can be the
     * root object.
     */
    test_referecedRoot() {
        let translator = new Translator();
        translator.classRegistry.registerPackage(PackageFile);
        let text1 = JSON.stringify(this.json.hasRoot);
        let object1 = translator.decode(text1).getRoot();
        let text2 = JSON.stringify(this.json.hasRef);
        let object2 = translator.decode(text2).getRoot();
        Assert.equals(object1, object2);
    }
    /**
     * Cached objects can be retrieved by reference.  This reference can be a
     * field object.
     */
    test_referecedField() {
        let translator = new Translator();
        translator.classRegistry.registerPackage(PackageFile);
        let text1 = JSON.stringify(this.json.hasRoot);
        let object1 = translator.decode(text1).getRoot();
        let text2 = JSON.stringify(this.json.hasField);
        let object2 = translator.decode(text2).getRoot();
        Assert.equals(object1, object2.t);
    }
    /**
     * Zero length arrays will be instantiated.
     */
    test_emptyArray() {
        let translator = new Translator();
        translator.classRegistry.registerPackage(PackageFile);
        let text = JSON.stringify(this.json.emptyArray);
        let object = translator.decode(text).getRoot();
        Assert.equals(true, object.t instanceof Array);
        Assert.equals(0, object.t.length);
    }
    /**
     * Arrays will be filled with decoded values.
     */
    test_nonEmptyArray() {
        let translator = new Translator();
        translator.classRegistry.registerPackage(PackageFile);
        let text = JSON.stringify(this.json.nonEmptyArray);
        let object = translator.decode(text).getRoot();
        Assert.equals(true, object.t instanceof Array);
        Assert.equals(3, object.t.length);
        Assert.equals(1, object.t[0]);
        Assert.equals(3, object.t[1]);
        Assert.equals(7, object.t[2]);
    }
    
    /**
     * Circular references will point to each other.
     */
    test_circular(){
        let translator = new Translator();
        translator.classRegistry.registerPackage(PackageFile);
        let text = JSON.stringify(this.json.circular);
        let object = translator.decode(text).getRoot();
        let target = object.target;
        
        Assert.equals("ca.frar.jjjrmi.testclasses.CircularRef", object.constructor.__getClass());
        Assert.equals(object, target.target);
    }
}