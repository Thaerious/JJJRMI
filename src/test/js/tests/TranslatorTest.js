/* global process */
"use strict";
"use strict";
const TestFramework = require("../Framework").TestFramework;
const Assert = require("../Framework").Assert;
const Translator = require("../../../main/js/translator/Translator");
const packageFile = require("../testclasses/packageFile");

class TranslatorTest extends TestFramework{
    constructor(){
        super();
    }
    
    test_simple(){
        let translator = new Translator();
        translator.registerPackage(packageFile);
        
        let hasHandler = new packageFile.Simple();
        let encoded = translator.encode(hasHandler);       
        
        translator.clear();
        let result = translator.decode(encoded.toString());
        let object = result.getRoot();
        
        Assert.equals(5, object.x);
        Assert.equals(7, object.y);
        Assert.equals(packageFile.Shapes.CIRCLE, object.shape);
    }    
    
    test_hasHandler(){
        let translator = new Translator();
        translator.registerPackage(packageFile);
        
        let hasHandler = new packageFile.HasHandler(2, 5);
        let tResult = translator.encode(hasHandler);
        
        translator.clear();
        let result = translator.decode(tResult.toString());
        
        Assert.equals(7, result.getRoot().z);
    }
    test_retain(){
        let translator = new Translator();
        translator.registerPackage(packageFile);
        
        let object = new packageFile.None();
        let tResult = translator.encode(object);
        let decoded = translator.decode(tResult.toString());
        Assert.equals(object, decoded.getRoot());
    }
    test_doNotRetain(){
        let translator = new Translator();
        translator.registerPackage(packageFile);
        
        let object = new packageFile.NoRetain();
        let tResult = translator.encode(object);
        let decoded = translator.decode(tResult.toString());
        Assert.notEquals(object, decoded.getRoot());
    }    
    /**
     * Transient field do not apply to objects both encoded and decoded in 
     * javascript.  This behaviour may change.  The transient field should not
     * be decoded into java even when it exists in the encoded string.
     *
     * @author Ed Armstrong
     */
    test_transient_field(){
        let translator = new Translator();
        translator.registerPackage(packageFile);
        let object = new packageFile.TransientField();
        object.set(9);
        let encoded = translator.encode(object);
        translator.clear();
        let decoded = translator.decode(encoded.toString()).getRoot();
        Assert.equals(decoded.getTransientField(), object.getTransientField());
        Assert.equals(decoded.getNonTransientField(), object.getNonTransientField());
    }

};

module.exports = TranslatorTest;