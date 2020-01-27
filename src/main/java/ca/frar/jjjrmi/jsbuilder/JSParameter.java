/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.jsbuilder;

import ca.frar.jjjrmi.jsbuilder.code.AbstractJSCodeElement;

/**
 *
 * @author Ed Armstrong
 */
public class JSParameter extends AbstractJSCodeElement{
    String name = "";
    String initializer = "";
    
    public JSParameter(String name, String initializer){
        this.name = name;
        this.initializer = initializer;
    }
    
    public String toString(){
        if (!initializer.isBlank()){
            return name + " = " + initializer;
        } else {
            return name;
        }
    }
    
}
