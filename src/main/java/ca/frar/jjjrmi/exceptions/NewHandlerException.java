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
public class NewHandlerException extends TranslatorException {

    public NewHandlerException(String message) {
        super(message);
    }

    public NewHandlerException(Exception ex) {
        super(ex);
    }
    
}
