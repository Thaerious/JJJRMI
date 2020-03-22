/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.translator.testclasses;

import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.rmi.socket.JJJObject;

/**
 *
 * @author Ed Armstrong
 */
public class HasHandler extends JJJObject{
    public int x;
    public float y;
    public float z;
    
    @NativeJS
    public HasHandler(int x, float y){
        this.x = x;
        this.y = y;
        this.z = x * y;
    }
    
    public HasHandler(){
    }    
}