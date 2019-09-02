const Translator = require("../../main/js/Translator");

const translator = new Translator();

let snafu = {
    a : 1
};

let myObject = {
    snafu : snafu
};

let encoded = translator.encode(myObject);
let jsonEncoded = JSON.stringify(encoded, null, 2);
console.log(jsonEncoded);