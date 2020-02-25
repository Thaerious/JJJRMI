/* global process, Reflect */
"use strict";
const fs = require("fs");
const filename = process.argv[2];
const method = process.argv[3];
const Translator = require("../../main/js/translator/Translator");
const PackageFile = require("./testclasses/packageFile");

function getAllMethodNames(obj) {
    let methods = new Set();
    while (obj) {
        let keys = Reflect.ownKeys(obj);
        keys.forEach((k) => methods.add(k));
        obj = Reflect.getPrototypeOf(obj);
    }
    return methods;
}

class GenerateJSON {
    constructor() {
        this.json = {};
    }
    run() {
        let methods = getAllMethodNames(this);
        for (let method of methods) {
            if (!method.startsWith("generate_")) continue;
            let r = this[method]();
            if (r) this.json[method.substring(9)] = r;
        }
    }
    build(method){
        let r = this[method]();
        if (r) this.json[method.substring(9)] = r;
    }
    
    generate_circular(){
        let translator = new Translator();
        return translator.encode(new PackageFile.CircularRef());
    }
    
    generate_referenceAsRoot(){
        let translator = new Translator();
        let has = new PackageFile.Has(null);
        
        this.json['hasRoot'] = translator.encode(has).toJSON();
        this.json['hasRef'] = translator.encode(has).toJSON();
        this.json['hasField'] = translator.encode(new PackageFile.Has(has)).toJSON();
        
        return null;
    }    
    
    generate_nonEmptyArray(){
        let translator = new Translator();
        let array = [1, 3, 7];
        return translator.encode(new PackageFile.Has(array));
    }        
    
    generate_emptyArray(){
        let translator = new Translator();
        let array = [];
        return translator.encode(new PackageFile.Has(array));
    }    
    
    generate_hasNull(){
        let translator = new Translator();
        return translator.encode(new PackageFile.Has(null));
    }
    
    generate_primitivesExtended(){
        let translator = new Translator();
        return translator.encode(new PackageFile.PrimitivesExtended(16));
    }

    generate_simple(){
        let translator = new Translator();
        return translator.encode(new PackageFile.Simple());
    }

    generate_none(){
        let translator = new Translator();
        return translator.encode(new PackageFile.None());
    }

    generate_primitives(){
        let translator = new Translator();
        return translator.encode(new PackageFile.Primitives(9));
    }

    generate_arrayWrapper(){
        let translator = new Translator();
        return translator.encode(new PackageFile.Primitives(9));
    }    
}

let generateJSON = new GenerateJSON();

if (method) generateJSON.build(method);
else generateJSON.run();

fs.writeFileSync(filename, JSON.stringify(generateJSON.json, null, 2));