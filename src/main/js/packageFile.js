"use strict";

let JJJRMI = {
    handlers : {
        HashMap : require("./handlers/HashMap")
    },
    javaEquivalent : {
        ArrayList : require("./java-equiv/ArrayList")        
    },
    JJJRMISocket : require("./JJJRMISocket")
};

module.exports = JJJRMI;