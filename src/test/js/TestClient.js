"use strict";
const WebSocket = require('ws');

console.log("JS connecting...");
const ws = new WebSocket("ws://127.0.0.1:8000");

ws.on("open", function open(){
    console.log("open");
    ws.send("something");
});

ws.on("message", function message(data){
    console.log("js");
    console.log(data);
});

