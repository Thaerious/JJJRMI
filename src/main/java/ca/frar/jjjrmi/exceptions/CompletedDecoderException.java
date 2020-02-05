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
public class CompletedDecoderException extends DecoderException{
    
    public CompletedDecoderException(String message) {
        super(message);
    }

    public CompletedDecoderException() {
        super("");
    }
    
}
