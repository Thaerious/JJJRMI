/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.jsbuilder;

import ca.frar.jjjrmi.jsbuilder.code.AbstractJSCodeElement;
import java.util.Objects;

/**
 *
 * @author Ed Armstrong
 */
public class JSParameter extends AbstractJSCodeElement{
    String name = "";
    String initializer = "";
    
    public JSParameter(String name, String initializer){
        this.name = name;
        if (initializer != null) this.initializer = initializer;
    }

    /**
     * Case insensitive comparison of two parameters by name.
     * @param object
     * @return 
     */
    @Override
    public boolean equals(Object object){
        if (object instanceof JSParameter == false) return false;
        JSParameter that = (JSParameter) object;
        return this.name.toLowerCase().equals(that.name.toLowerCase());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.name);
        return hash;
    }
   
    public String toString(){
        if (!initializer.isBlank()){
            return name + " = " + initializer;
        } else {
            return name;
        }
    }
    
}
