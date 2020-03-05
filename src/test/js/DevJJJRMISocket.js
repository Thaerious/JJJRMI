"use strict";

const JJJRMISocket = require("jjjrmi").JJJRMISocket;
const testPackage = require("./socket-testclasses/packageFile.js");
const LOGGER = require("jjjrmi").Logger;
const OperationAdd = testPackage.OperationAdd;
const OperationMultiply = testPackage.OperationMultiply;
const Instruction = testPackage.Instruction;

LOGGER.flags = {
    DEBUG: true,      /* print all debug messages */
    EXCEPTION: true,  /* print exceptions to console */
    CONNECT: true,    /* show the subset of ONMESSAGE that deals with the initial connection */
    ONMESSAGE: false, /* describe the action taken when a message is received */
    SENT: false,      /* show the send object, versbose shows the json text as well */
    RECEIVED: false,   /* show all received server objects, verbose shows the json text as well */
    ONREGISTER: true, /* report classes as they are registered */
    WARN: true        /* report classes as they are registered */
};

async function run1(){
    let socket = new JJJRMISocket("test");
    socket.registerPackage(testPackage);
    let root = await socket.connect("ws://127.0.0.1:8000");
    
    let data = await root.getData();
    
    data.onUpdate = function(value){
        console.log("new top value: " + value);
    }
    
//    console.log(data);
    
    data.clear();

    await data.push(0);
    let add = new OperationAdd(5);
    console.log(add);
    data.op(add);
    data.op(add);
        
    data.inst(Instruction.MEM_SAVE);
    data.clear();
    data.inst(Instruction.MEM_PUSH);
    
    data.inst(null);
    
    socket.close();
}

run1();
