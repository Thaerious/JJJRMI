"use strict";

const JJJRMISocket = require("jjjrmi").JJJRMISocket;
const testPackage = require("./socket-testclasses/packageFile.js");

async function run1(){
    let socket = new JJJRMISocket("test");
    socket.registerPackage(testPackage);
    let root = await socket.connect("ws://127.0.0.1:8000");
    
    root.setPersistantValue(5);
    let pv = await root.getPersistantValue();
    console.log(pv);
    
    await root.mirrorPersistantValue(7);
    console.log(root.value);
}

async function run2(){
    let socket = new JJJRMISocket("test");
    socket.registerPackage(testPackage);
    let root = await socket.connect("ws://127.0.0.1:8000");
    
    let pv = await root.getPersistantValue();
    console.log(pv);
}

run1();
run2();