/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.exceptions;

/**
 *
 * @author Ed Armstrong
 */
public class UnknownParameterException extends JJJRMIException {

    public UnknownParameterException(String name) {
        super("Unknown parmeter: " + name);
    }
    
}
