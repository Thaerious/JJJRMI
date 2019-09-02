const readline = require('readline');
const Translator = require("../../main/js/Translator");

let rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout,
  terminal: false
});

let translator = new Translator();

rl.on('line', function(line){    
    let decoded = translator.decode(line);
    let encoded = translator.encode(decoded);
    console.log(encoded);
    process.exit(0);
});

