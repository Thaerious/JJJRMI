/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.testclasses;

import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.socket.JJJObject;
import java.util.function.Consumer;

/**
 *
 * @author Ed Armstrong
 */
public class JSLambdaCode extends JJJObject{
    
    @NativeJS
    public void takesLambda(Consumer<String> consumer){
        consumer.accept("ima string");
    }
    
    @NativeJS
    public void callsLambda1(){
        this.takesLambda((e)->e.toString());
    }

    @NativeJS
    public void callsLambda2(){
        this.takesLambda((e)->{
            e = e.concat(" i really am");
            e.toString();
        });
    }
    
}
