const readline = require('readline');
const Translator = require("../../main/js/Translator");

let translator = new Translator();
let input = `{"retain":false,"type":"ca.frar.jjjrmi.test.testable.HasInt","fields":{"x":{"primitive":"number","value":0}},"key":"S0"}`;
let decoded = translator.decode(input);

console.log(decoded);
console.log(decoded.constructor.__isTransient());

let encoded = translator.encode(decoded);
console.log(JSON.stringify(encoded, null, 2));

