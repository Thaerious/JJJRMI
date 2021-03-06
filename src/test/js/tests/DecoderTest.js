/**
 * Test decoding json that was generated by java.
 * pre> java -cp target/classes/:target/test-classes/:target/dependency/* ca.frar.jjjrmi.translator.GenerateJSON ./target/test-data/from-java.json
 * > node node src/test/js/TestJavaToJS ./target/test-data/from-java.json
 * > node node src/test/js/TestJavaToJS [file-name]
 * > node node src/test/js/TestJavaToJS [file-name] [test-name]
 *
 * Will output the failed tests, and the counts.
 */

/* global process, Reflect */
"use strict";
const TestFramework = require("../Framework").TestFramework;
const Assert = require("../Framework").Assert;
const Translator = require("../../../main/js/translator/Translator");
const PackageFile = require("../testclasses/packageFile");

class DecoderTest extends TestFramework{
    constructor(json) {
        super();
        this.json = json;
    }

    /**
     * Sanity test to make sure the translator loads and processes.
     */
    test_none() {
        let translator = new Translator();
        translator.registerPackage(PackageFile);
        let text = JSON.stringify(this.json.none);
        let object = translator.decode(text).getRoot();
        Assert.equals("ca.frar.jjjrmi.translator.testclasses.None", object.constructor.__getClass());
    }

    /**
     * A class will be populated with default values.
     */
    test_simple() {
        let translator = new Translator();
        translator.registerPackage(PackageFile);
        let text = JSON.stringify(this.json.simple);
        let object = translator.decode(text).getRoot();

        Assert.equals("ca.frar.jjjrmi.translator.testclasses.Simple", object.constructor.__getClass());
        Assert.equals(5, object.x);
        Assert.equals(7, object.y);
        Assert.equals(PackageFile.Shapes.CIRCLE, object.shape);
    }
    /**
     * The default values of a class will be overwritten by the data.
     */
    test_primitives() {
        let translator = new Translator();
        translator.registerPackage(PackageFile);
        let text = JSON.stringify(this.json.primitives);
        let object = translator.decode(text).getRoot();
        Assert.equals("ca.frar.jjjrmi.translator.testclasses.Primitives", object.constructor.__getClass());
        Assert.equals("alpha9", object.string);
    }
    /**
     * Classes that indirectly extend JJJObject will include exteded fields.
     */
    test_primitivesExtended() {
        let translator = new Translator();
        translator.registerPackage(PackageFile);
        let text = JSON.stringify(this.json.primitivesExtended);
        let object = translator.decode(text).getRoot();
        Assert.equals("ca.frar.jjjrmi.translator.testclasses.PrimitivesExtended", object.constructor.__getClass());
        Assert.equals("alpha16", object.string);
    }
    /**
     * Null fields will be instantiated with null.
     */
    test_hasNull() {
        let translator = new Translator();
        translator.registerPackage(PackageFile);
        let text = JSON.stringify(this.json.hasNull);
        let object = translator.decode(text).getRoot();
        Assert.equals("ca.frar.jjjrmi.translator.testclasses.Has", object.constructor.__getClass());
        Assert.equals(null, object.t);
    }

    /**
     * Decoding the same object twice will return the same object.
     */
    test_same() {
        let translator = new Translator();
        translator.registerPackage(PackageFile);
        let text = JSON.stringify(this.json.none);
        let object1 = translator.decode(text).getRoot();
        let object2 = translator.decode(text).getRoot();
        Assert.equals(object1, object2);
    }

    /**
     * Cached objects can be retrieved by reference.  This reference can be the
     * root object.
     */
    test_referecedRoot() {
        let translator = new Translator();
        translator.registerPackage(PackageFile);
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
        translator.registerPackage(PackageFile);
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
        translator.registerPackage(PackageFile);
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
        translator.registerPackage(PackageFile);
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
        translator.registerPackage(PackageFile);
        let text = JSON.stringify(this.json.circular);
        let object = translator.decode(text).getRoot();
        let target = object.target;

        Assert.equals("ca.frar.jjjrmi.translator.testclasses.CircularRef", object.constructor.__getClass());
        Assert.equals(object, target.target);
    }

    /**
     * A class with a registered handler, will invoke the handler methods to
     * decode instead of the default.
     * @returns {undefined}
     */
    test_hasHandler(){
        let translator = new Translator();
        translator.registerPackage(PackageFile);
        let text = JSON.stringify(this.json.handled);
        let object = translator.decode(text).getRoot();

        Assert.equals("ca.frar.jjjrmi.translator.testclasses.HasHandler", object.constructor.__getClass());
        Assert.equals(9, object.z);
    }
    /**
     * The encoded object both extends JJJObject and has the annotation.
     * The object will not be tracked by the translator, and decoding will
     * produce a new object.
     * @throws JJJRMIException
     */
    test_doNotRetainExtends(){
        let translator = new Translator();
        translator.registerPackage(PackageFile);
        let text = JSON.stringify(this.json.doNotRetainExtends);
        let object1 = translator.decode(text).getRoot();
        let object2 = translator.decode(text).getRoot();

        Assert.equals("ca.frar.jjjrmi.translator.testclasses.DoNotRetainExtends", object1.constructor.__getClass());
        Assert.equals("ca.frar.jjjrmi.translator.testclasses.DoNotRetainExtends", object2.constructor.__getClass());
        Assert.notEquals(object1, object2);
    }
    
/**
     * The encoded object both extends JJJObject and has the annotation.
     * The object will not be tracked by the translator, and decoding will
     * produce a new object.
     * @throws JJJRMIException
     */
    test_doNotRetainAnno(){
        let translator = new Translator();
        translator.registerPackage(PackageFile);
        let text = JSON.stringify(this.json.doNotRetainAnno);
        let object1 = translator.decode(text).getRoot();
        let object2 = translator.decode(text).getRoot();

        Assert.equals("ca.frar.jjjrmi.translator.testclasses.DoNotRetainAnno", object1.constructor.__getClass());
        Assert.equals("ca.frar.jjjrmi.translator.testclasses.DoNotRetainAnno", object2.constructor.__getClass());
        Assert.notEquals(object1, object2);
    }

    test_has_after_decode(){
        let translator = new Translator();
        translator.registerPackage(PackageFile);
        let text = JSON.stringify(this.json.hasAfterDecode, null, 2);
        let object = translator.decode(text).getRoot();

        Assert.equals(10, object.get());
    }
}

module.exports = DecoderTest;