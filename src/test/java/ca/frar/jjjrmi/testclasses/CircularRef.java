/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.testclasses;

import ca.frar.jjjrmi.annotations.JJJ;

/**
 *
 * @author Ed Armstrong
 */
@JJJ
public class CircularRef {
    private final CircularRef target;
 
    public CircularRef(){
        this.target = new CircularRef(new CircularRef(this));
    }
    
    public CircularRef(CircularRef target){
        this.target = target;
    }
    
}
