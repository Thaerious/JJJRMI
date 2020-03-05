/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.socket.testclasses;
import ca.frar.jjjrmi.annotations.ServerSide;
import ca.frar.jjjrmi.annotations.Transient;
import ca.frar.jjjrmi.socket.JJJObject;

/**
 *
 * @author Ed Armstrong
 */
public class TestRoot extends JJJObject{
    @Transient private RemoteData data = new RemoteData();
    
    @ServerSide
    public RemoteData getData(){
        return data;
    }

    @ServerSide
    public void reset(){
        data = new RemoteData();
    }        
}
