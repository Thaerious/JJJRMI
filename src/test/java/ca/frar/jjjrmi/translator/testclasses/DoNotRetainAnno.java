/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.translator.testclasses;

import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;

/**
 *
 * @author Ed Armstrong
 */
@JJJ(retain=false)
public class DoNotRetainAnno{
    private int x = 0;
    
    @NativeJS
    public DoNotRetainAnno(int x){
        this.x = x;
    }
        
    public DoNotRetainAnno(){
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
