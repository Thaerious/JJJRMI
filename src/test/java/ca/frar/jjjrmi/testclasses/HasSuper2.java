package ca.frar.jjjrmi.testclasses;



import ca.frar.jjjrmi.testclasses.*;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;

/** 
 * Check to make sure super doesn't go into the constuctor twice;
 * @author Ed Armstrong
 */

@JJJ
public class HasSuper2 extends None{
    private final int x;
    
    @NativeJS
    public HasSuper2(){
        super();
        this.x = 5;
    }   
}