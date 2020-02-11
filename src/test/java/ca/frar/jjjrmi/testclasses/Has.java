/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.testclasses;

import ca.frar.jjjrmi.socket.JJJObject;

/**
 *
 * @author Ed Armstrong
 */
public class Has <T> extends JJJObject{
    private T t;
    
    private Has(){}
    
    public Has(T t){
        this.t = t;
    }
    
    public void set(T t){
        this.t = t;
    }
    
    public T get(){
        return t;
    }
}
