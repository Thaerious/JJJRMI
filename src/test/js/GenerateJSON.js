/* global process, Reflect */
"use strict";
const fs = require("fs");
const Translator = require("../../main/js/translator/Translator");
const packageFile = require("./testclasses/packageFile");

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
        return translator.encode(new packageFile.CircularRef());
    }

    generate_referenceAsRoot(){
        let translator = new Translator();
        let has = new packageFile.Has(null);

        this.json['hasRoot'] = translator.encode(has).toJSON();
        this.json['hasRef'] = translator.encode(has).toJSON();
        this.json['hasField'] = translator.encode(new packageFile.Has(has)).toJSON();

        return null;
    }

    generate_handled(){
        let translator = new Translator();
        translator.registerPackage(packageFile);
        return translator.encode(new packageFile.HasHandler(2, 7));
    }

    generate_nonEmptyArray(){
        let translator = new Translator();
        let array = [1, 3, 7];
        return translator.encode(new packageFile.Has(array));
    }

    generate_emptyArray(){
        let translator = new Translator();
        let array = [];
        return translator.encode(new packageFile.Has(array));
    }

    generate_hasNull(){
        let translator = new Translator();
        return translator.encode(new packageFile.Has(null));
    }

    generate_primitivesExtended(){
        let translator = new Translator();
        return translator.encode(new packageFile.PrimitivesExtended(16));
    }

    generate_simple(){
        let translator = new Translator();
        return translator.encode(new packageFile.Simple());
    }

    generate_none(){
        let translator = new Translator();
        return translator.encode(new packageFile.None());
    }

    generate_primitives(){
        let translator = new Translator();
        return translator.encode(new packageFile.Primitives(9));
    }

    generate_arrayWrapper(){
        let translator = new Translator();
        return translator.encode(new packageFile.Primitives(9));
    }
    
    generate_doNotRetainExtends(){
        let translator = new Translator();
        return translator.encode(new packageFile.DoNotRetainExtends(5));
    }
    
    generate_doNotRetainAnno(){
        let translator = new Translator();
        return translator.encode(new packageFile.DoNotRetainAnno(5));
    }        
    generate_transientField(){
        let translator = new Translator();
        return translator.encode(new packageFile.TransientField().set(9));
    }      
}

const filename = process.argv[2];
const method = process.argv[3];

if (filename.indexOf("/" !== -1)){
    let index = filename.lastIndexOf("/");
    let dir = filename.substr(0, index);
    if (!fs.existsSync(dir)) fs.mkdirSync(dir);
}

let generateJSON = new GenerateJSON();

if (method) generateJSON.build(method);
else generateJSON.run();

console.log("writing JS data file to " + filename);
fs.writeFileSync(filename, JSON.stringify(generateJSON.json, null, 2));