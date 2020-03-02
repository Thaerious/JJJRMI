/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.testclasses;

import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.socket.JJJObject;

/**
 *
 * @author Ed Armstrong
 */
@JJJ(retain=false)
public class DoNotRetainExtends extends JJJObject{
    private int x = 0;
    
    @NativeJS
    public DoNotRetainExtends(int x){
        this.x = x;
    }    
    
    public DoNotRetainExtends(){
    }
    
    @NativeJS
    public void setX(int x){
        this.x = x;
    }
    
    @NativeJS
    public int getX(){
        return x;
    }    
}
