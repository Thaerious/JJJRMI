/* global process */
"use strict";
"use strict";
const TestFramework = require("./Framework").TestFramework;
const Assert = require("./Framework").Assert;
const Translator = require("../../main/js/translator/Translator");
const packageFile = require("./testclasses/packageFile");

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
};

const test = new TranslatorTest();
test.start(process.argv.slice(2));