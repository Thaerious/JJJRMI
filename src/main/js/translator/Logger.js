"use strict";

class Logger {
    static log(category, message){
        if (!Logger.checkFlags(category)) return;
        console.log(message);
    }
    
    static checkFlags(categories){
        for (let cat of categories.split(/[, \t]+/g)){
            if (cat.endsWith("+")){
                let flag = cat.substr(0, cat.length - 1);
                if (Logger.flags[flag.toUpperCase()] === "verbose") return true; 
            } else {
                if (Logger.flags[cat.toUpperCase()]) return true; 
            }
        }
        return false;
    }
    
    static warn(message){
        Logger.log("WARN", message);
    }    
        
    static debug(message){
        Logger.log("DEBUG", message);
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
    WARN: true, /* report classes as they are registered */
    TRANSLATOR: false /* report translator decisions */
};

module.exports = Logger;
