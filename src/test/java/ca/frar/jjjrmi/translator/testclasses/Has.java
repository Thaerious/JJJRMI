/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.translator.testclasses;

import ca.frar.jjjrmi.annotations.JSParam;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.socket.JJJObject;

/**
 *
 * @author Ed Armstrong
 */
public class Has <T> extends JJJObject{
    private T t;
    
    public Has(){}
    
    @NativeJS
    @JSParam(name="t", init="undefined")
    public Has(T t){
        this.t = t;
    }
    
    @NativeJS
    public void set(T t){
        this.t = t;
    }
    
    @NativeJS
    public T get(){
        return t;
    }
}
