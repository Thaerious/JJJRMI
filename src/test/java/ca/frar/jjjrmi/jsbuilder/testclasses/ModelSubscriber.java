/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.jsbuilder.testclasses;

import java.io.Serializable;

/**
 *
 * @author Ed Armstrong
 */
public interface ModelSubscriber extends Serializable{

    /**
     * Notify the subscriber that an event with name 'name' occurred.
     * @param name
     * @param value 
     */
    void notify(Object ... value);
    
}
