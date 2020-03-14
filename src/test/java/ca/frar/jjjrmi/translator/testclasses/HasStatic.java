/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.translator.testclasses;

import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.socket.JJJObject;

/**
 *
 * @author Ed Armstrong
 */
public class HasStatic extends JJJObject{
    
    static int iAmInteger = 5;
    int iAmNotStatic = 6;
    
    @NativeJS
    public static void iAmMethod(){
    }
    
}
