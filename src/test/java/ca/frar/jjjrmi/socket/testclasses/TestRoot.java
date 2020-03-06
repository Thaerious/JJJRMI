/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.socket.testclasses;
import ca.frar.jjjrmi.annotations.ServerSide;
import ca.frar.jjjrmi.annotations.Transient;
import ca.frar.jjjrmi.socket.JJJObject;
import java.util.ArrayList;

/**
 *
 * @author Ed Armstrong
 */
public class TestRoot extends JJJObject{
    @Transient private RemoteStack data;
    
    @ServerSide
    public RemoteStack setData(ArrayList list){
        this.data = new RemoteStack(list);
        return this.data;
    }    

}
