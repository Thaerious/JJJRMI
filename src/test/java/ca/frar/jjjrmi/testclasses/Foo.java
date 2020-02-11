/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.testclasses;

import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.socket.JJJObject;

/**
 *
 * @author Ed Armstrong
 */
public class Foo extends JJJObject{   
    int x = 0;
    
    private Foo(){}
    
    @NativeJS
    public Foo(int x){
        this.x = x;
    }
}
