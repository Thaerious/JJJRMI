/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.testclasses;

import ca.frar.jjjrmi.annotations.DoNotPackage;
import ca.frar.jjjrmi.annotations.JSRequire;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.socket.JJJObject;
import java.util.ArrayList;

/**
 *
 * @author Ed Armstrong
 */
@DoNotPackage
public class UnknownNonJJJ extends JJJObject{
    private final ArrayList array;
    
    @NativeJS
    public UnknownNonJJJ(){
        this.array = new ArrayList();
    }
    
}
