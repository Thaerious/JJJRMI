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
public class HasHandler {
    int x;
    float y;
    float z;
    
    public HasHandler(int x, float y){
        this.x = x;
        this.y = y;
        this.z = x * y;
    }
}