"use strict";
const fs = require("fs");
const filename = process.argv[2];

let list = function(object){
    for (let field in object) console.log(field);
}

let text = fs.readFileSync(filename);
let json = JSON.parse(text);

let current = json;
for (let i = 3; i < process.argv.length; i++) {
    let command = process.argv[i];
    let selectors = command.trim().split(/ +/);

    for (let selector of selectors) {
        switch (selector) {
            case ".list":
                list(current);
                break;
            case ".print":
                console.log(JSON.stringify(current, 0, 2));
                break;
            default:
                current = current[selector];
                break;
        }
    }
}
