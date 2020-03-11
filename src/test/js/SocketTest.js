/* global process */
"use strict";

const TestFramework = require("./Framework").TestFramework;
const Assert = require("./Framework").Assert;
const Translator = require("../../main/js/translator/Translator");
const JJJRMISocket = require("jjjrmi").JJJRMISocket;

const testPackage = require("./socket-testclasses/packageFile");
const ArrayList = require("./ext/ArrayList");
const LOGGER = require("jjjrmi").Logger;
const OperationAdd = testPackage.OperationAdd;
const OperationMultiply = testPackage.OperationMultiply;
const Instruction = testPackage.Instruction;
const MemOpCode = testPackage.MemOpCode;

LOGGER.flags = {
    DEBUG: true, /* print all debug messages */
    EXCEPTION: true, /* print exceptions to console */
    CONNECT: true, /* sfghow the subset of ONMESSAGE that deals with the initial connection */
    ONMESSAGE: false, /* describe the action taken when a message is received */
    SENT: false, /* show the send object, versbose shows the json text as well */
    RECEIVED: false, /* show all received server objects, verbose shows the json text as well */
    ONREGISTER: false, /* report classes as they are registered */
    WARN: true        /* report classes as they are registered */
};

class SocketTest extends TestFramework{
    constructor(){
        super();
    }
    
    /*
     * Connect a single client, they will recieve a "TestRoot" object.
     */
    async test_connect_first(){
        this.socket1 = new JJJRMISocket("test-socket1");
        this.socket1.registerPackage(testPackage);
        let testRoot = await this.socket1.connect("ws://127.0.0.1:8000");
        Assert.true(testRoot !== null);
    }
    
    /*
     * Connect a single client, they will recieve a "TestRoot" object.  This 
     * object will not be the same as that returned to the first client (they
     * are the same server side, not client side).
     */    
    async test_connect_second(){
        this.socket2 = new JJJRMISocket("test-socket2");
        this.socket2.registerPackage(testPackage);
        let testRoot = await this.socket2.connect("ws://127.0.0.1:8000");
        Assert.true(testRoot !== null);
        Assert.false(this.socket1.root === this.socket2.root);
    }
    
    /*
     * Send an arraylist to the server to use as data.  The summary getData
     * will be same.
     */
    async test_send_object(){
        let arrayList = new ArrayList();
        await this.socket1.root.setData(arrayList);
        Assert.equals(arrayList, await this.socket1.root.getData());
    }
    
    /*
     * Send the same arraylist to the server to use as data.  No changes should
     * be seen.
     */
    async test_send_object_again(){
        let arrayList = new ArrayList();
        await this.socket1.root.setData(arrayList);
        Assert.equals(arrayList, await this.socket1.root.getData());
    }    
    
    /*
     * The stacks retrieved by each socket will be different.
     */
    async test_get_stack(){
        this.stack1 = await this.socket1.root.getStack();
        this.stack2 = await this.socket2.root.getStack();
        Assert.notEquals(this.stack1, this.stack2);
    }    
    
    /*
     * Push, changes a value on a shared object.  All sockets will see this
     * change.  Note, the call will not return until all clients have been sent
     * the return value message.  There is no guarantee that they have all 
     * received it.
     */
    async test_rmi(){
        await this.stack1.push(5);
        Assert.equals(this.stack1.peek, this.stack2.peek);
    }
    
    /**
     * Check the remote sizes.
     * @returns {undefined}
     */
    async test_size(){
        Assert.equals(1, await this.stack1.size());
        Assert.equals(1, await this.stack2.size());
    }
    
    /*
     * Send an enum to the server.
     */
    async test_enum(){
        await this.stack1.mem(MemOpCode.SAVE);
        await this.stack1.push(7);
        await this.stack1.mem(MemOpCode.RESTORE);
        let value = await this.stack1.pop();
        Assert.equals(5, value);
    }
    
    /*
     * Send a null to the server, throws an exception.
     */
    async test_null(){
        let err = null;
        
        try{
            await this.stack1.mem(null);
        } catch (error){
            err = error;
        }
        Assert.equals("ServerSideExceptionMessage", err.constructor.name);
    }
    
    async test_close(){        
        await this.socket1.close();
        await this.socket2.close();        
    }    
}

const test = new SocketTest();
test.start(process.argv.slice(2));
