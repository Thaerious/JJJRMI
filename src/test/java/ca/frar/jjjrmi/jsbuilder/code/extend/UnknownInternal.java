/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.jsbuilder.code.extend;

import ca.frar.jjjrmi.annotations.JSRequire;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.socket.JJJObject;
import java.util.ArrayList;

/**
 *
 * @author Ed Armstrong
 */
public class UnknownInternal extends JJJObject{
    private final NonJJJ nonjjj;
    
    @NativeJS
    public UnknownInternal(){
        this.nonjjj = new NonJJJ();
    }
    
}
