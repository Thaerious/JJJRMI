"use strict";
const WebSocket = require('ws');

console.log("JS connecting...");
const ws = new WebSocket("ws://127.0.0.1:8000");

function run(){
    console.log("open");
    ws.send("ima message!");
    ws.send("so am I!");
}

ws.on("open", function open(){
    run();
});

ws.on("message", function message(data){
    console.log("js> " + data);
});

