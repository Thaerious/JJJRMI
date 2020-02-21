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
public class UntrackedObjectException extends TranslatorException {

    private final Object object;

    public UntrackedObjectException(Object object) {
        super("Untracked object of type: " + object.getClass());
        this.object = object;
    }

    /**
     * @return the object
     */
    public Object getObject() {
        return object;
    }
    
}
