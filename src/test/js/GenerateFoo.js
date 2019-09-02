const Translator = require("../../main/js/Translator");
const Foo = require("./Foo");

let translator = new Translator();
translator.registerClass(Foo);
let foo = new Foo(5);
let encoded = translator.encode(foo);
console.log(JSON.stringify(encoded, null, 2));