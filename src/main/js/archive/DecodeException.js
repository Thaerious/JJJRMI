"use strict";

class DecodeException extends Error{

    constructor(message, prevException){
        super(message);
        
        if (prevException){
            this.jjjstack = prevException.jjjstack;
        }
        
        if (this.jjjstack === undefined){
            this.jjjstack = [];
            if (prevException) this.jjjstack.push(prevException.message);            
        }
        
        this.jjjstack.push(message); 
    }
    
    printStack(){
        for (let line of this.jjjstack){
            console.error(line);
        }        
    }
    
};

module.exports = DecodeException;