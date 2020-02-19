/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.testclasses.charcode;

import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;

/**
 *
 * @author Ed Armstrong
 */
@JJJ
public class InConstructor {
    private final char a;
    private final char b;
    private final char c;
    
    public static void main(String ... args){
        System.out.println(new InConstructor().toString());
    }
    
    @NativeJS
    public InConstructor(){
        this.a = 'a';
        this.b = 66;
        this.c = (short)66;
    }
    
    @Override
    public String toString(){
        return "[" + a + ", " + b + "]";
    }
    
}
