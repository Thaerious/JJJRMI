let TestFramework = require("./src/test/js/Framework").TestFramework;
let Assert = require("./src/test/js/Framework").Assert;
let Translator = require("./src/main/js/translator/Translator");
let packageFile = require("./src/test/js/testclasses/packageFile");

let t = new Translator();
t.registerPackage(packageFile);