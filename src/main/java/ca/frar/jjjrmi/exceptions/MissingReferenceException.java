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
public class MissingReferenceException extends DecoderException {

    public MissingReferenceException() {
        super("Reference not found.");
    }
    
    public MissingReferenceException(String reference) {
        super("Reference not found: " + reference);
    }    
    
}
