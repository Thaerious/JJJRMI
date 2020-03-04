/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.socket.testclasses;
import ca.frar.jjjrmi.annotations.NativeJS;
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
     * The @ServerSide indicates the client can call this method and it will be
     * invoked on server.  The value is not mirrored in client side JS.
     * @param value 
     */
    @ServerSide
    public void setPersistantValue(int value){
        this.value = value;
    }

    /**
     * This method causes the server to invoke a client side method.
     * @param value 
     */
    @ServerSide
    public void mirrorPersistantValue(int value){
        this.value = value;
        this.invokeClientMethod("setValue", value);
    }
    
    /**
     * Retrieve the value from the server.
     * @param value
     */
    @ServerSide
    public int getPersistantValue(){
        return value;
    }    
    
    /**
     * This method will be available client side.  The mirrorPeristantValue
     * call will cause the server to invoke this method.
     */
    @NativeJS
    void setValue(int value){
        this.value = value;
    }
}
