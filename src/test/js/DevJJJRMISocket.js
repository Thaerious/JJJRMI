"use strict";

const JJJRMISocket = require("jjjrmi").JJJRMISocket;
const testPackage = require("./socket-testclasses/packageFile.js");

let socket = new JJJRMISocket("test");
socket.registerPackage(testPackage);
socket.connect("ws://127.0.0.1:8000");
