/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.translator.testclasses;

import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.annotations.Transient;
import ca.frar.jjjrmi.rmi.socket.JJJObject;

/**
 * Transient annotations prevent the field from being encoded.  When this object
 * is encoded the default value set in the constructor will remain, while any
 * value set after will be ignored.  The non-transient field will not be ignored.
 * @author Ed Armstrong
 */
public class TransientField extends JJJObject{
    @Transient private int x;
    private int y;
    
    @NativeJS
    public TransientField(){
        this.x = 5;
        this.y = 5;
    }

    @NativeJS
    public int getTransientField(){
        return x;
    }
    
    @NativeJS
    public int getNonTransientField(){
        return y;
    }    
    
    @NativeJS
    public TransientField set(int x){
        this.x = x;
        this.y = x;
        return this;
    }
}