"use strict";

const JJJRMISocket = require("jjjrmi").JJJRMISocket;
const testPackage = require("./socket-testclasses/packageFile.js");
const LOGGER = require("jjjrmi").Logger;

LOGGER.flags = {
    DEBUG: true, /* print all debug messages */
    EXCEPTION: true, /* print exceptions to console */
    CONNECT: true, /* show the subset of ONMESSAGE that deals with the initial connection */
    ONMESSAGE: false, /* describe the action taken when a message is received */
    SENT: false, /* show the send object, versbose shows the json text as well */
    RECEIVED: false, /* show all received server objects, verbose shows the json text as well */
    ONREGISTER: true, /* report classes as they are registered */
    WARN: true /* report classes as they are registered */
};

async function run1(){
    let socket = new JJJRMISocket("test");
    socket.registerPackage(testPackage);
    let root = await socket.connect("ws://127.0.0.1:8000");
    await root.mirrorPersistantValue(4);
    console.log(root.value);
    await root.setPersistantValue(6);
    console.log(root.value);
    let rv = await root.getPersistantValue();
    console.log(rv);
    socket.close();
}

run1();
