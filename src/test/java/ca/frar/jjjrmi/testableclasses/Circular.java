/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.testableclasses;

/**
 *
 * @author Ed Armstrong
 */
public class Circular {
    private final Circular target;
 
    public Circular(){
        this.target = new Circular(new Circular(this));
    }
    
    public Circular(Circular target){
        this.target = target;
    }
    
}
