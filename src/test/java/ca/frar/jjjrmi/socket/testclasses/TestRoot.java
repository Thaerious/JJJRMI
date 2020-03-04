/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.socket.testclasses;
import ca.frar.jjjrmi.annotations.ServerSide;
import ca.frar.jjjrmi.socket.JJJObject;

/**
 *
 * @author Ed Armstrong
 */
public class TestRoot extends JJJObject{
    private int value;
    
    /**
     * Set a value on the server, this will be persistent between sessions.
     * @param value 
     */
    @ServerSide
    public void setPersistantValue(int value){
        this.value = value;
    }    
}
