/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.jsbuilder.testclasses;

import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.socket.JJJObject;

/**
 *
 * @author Ed Armstrong
 */
public class Literals extends JJJObject{
    private final float x;
    private final Object y;
    private final long z;
    private final String w;
    private final char v;
    private static int a;
    private static int b;
    
    @NativeJS
    public Literals(){      
        this.v = 'a';
        this.w = "a";
        this.x = (float)1;
        this.y = null;
        this.z = 5L;
        this.a = 5;
        Literals.b = 6;
    }
}
