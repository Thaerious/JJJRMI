console.log("hello world 1");
console.log("hello world 2");
console.log("hello world 3");

var readline = require('readline');
var rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout,
  terminal: false
});

rl.on('line', function(line){    
    console.log(">" + line);  
    if (line === "exit") process.exit(0);
});