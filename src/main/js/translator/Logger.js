"use strict";

class Logger {
    static log(category, message){
        if (Logger.flags[category.toUpperCase()] === false) return;
        console.log(message);
    }
    
    static warn(message){
        Logger.log("WARN", message);
    }    
    
    static verbose(category, message){
        if (Logger.flags[category.toUpperCase()] !== "verbose") return;
        console.log(message);
    }
    
    static debug(message){
        if (Logger.flags.DEBUG) console.log(message);
    }    
}

Logger.flags = {
    DEBUG: false, /* print all debug messages */
    EXCEPTION: true, /* print exceptions to console */
    CONNECT: false, /* show the subset of ONMESSAGE that deals with the initial connection */
    ONMESSAGE: false, /* describe the action taken when a message is received */
    SENT: false, /* show the send object, versbose shows the json text as well */
    RECEIVED: false, /* show all received server objects, verbose shows the json text as well */
    ONREGISTER: false, /* report classes as they are registered */
    WARN: true /* report classes as they are registered */
};

module.exports = Logger;
