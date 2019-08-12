/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.jsbuilder.code;

/**
 *
 * @author Ed Armstrong
 */
public class AbstractJSCodeElement implements JSCodeElement{
    public boolean isEmpty() {
        return false;
    }
    public String scoped() {
        return toString();
    }    
}
