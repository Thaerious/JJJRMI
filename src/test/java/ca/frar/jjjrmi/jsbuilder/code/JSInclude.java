/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.jsbuilder.code;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.socket.JJJObject;

/**
 *
 * @author Ed Armstrong
 */
public class JSInclude extends JJJObject{
    
    JSLambdaCode callback;
    
    JSInclude(){}
    
    @NativeJS
    public void run(){
        this.callback = new JSLambdaCode();
    }
    
}