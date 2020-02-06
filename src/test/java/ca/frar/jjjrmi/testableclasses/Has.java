/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.testableclasses;

import ca.frar.jjjrmi.socket.JJJObject;

/**
 *
 * @author Ed Armstrong
 */
public class Has <T> extends JJJObject{
    private final T t;
    
    public Has(T t){
        this.t = t;
    }
    
}
