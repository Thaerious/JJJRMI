/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.testclasses;

/**
 * Uses hash code based on wrapped value.
 * @author Ed Armstrong
 */
public class CustomHash<T> extends Has<T>{
    
    public int hashCode(){
        return this.get().hashCode();
    }
}
