/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.testclasses;

import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.JSParam;
import ca.frar.jjjrmi.annotations.NativeJS;

/**
 * This class tests that the super invocation (w/params) gets created.
 * @author Ed Armstrong
 */
@JJJ
public class PrimitivesExtended extends Primitives{
    Primitives innerPrim;
    
    public PrimitivesExtended(){
        super();        
    }
    
    @NativeJS()
    @JSParam(name = "i", init = "7")    
    public PrimitivesExtended(int i) {
        super(i);
        foo();
        bar();
    }
    
    @NativeJS()
    public void foo(){
    }
    
    @NativeJS()
    public static void bar(){
    }    
}
