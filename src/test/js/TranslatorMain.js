const Translator = require("../../main/js/Translator");

const translator = new Translator();

let snafu = {
    a : 1
};

let myObject = {
    snafu : snafu,
};

let encoded = translator.encode(myObject);
console.log(encoded);