/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.testclasses;

/**
 *
 * @author Ed Armstrong
 */
public class ForcedDeferred {
    Has<Integer> hasInt1 = new Has<>(5);
    Has<Integer> hasInt2 = hasInt1;    
}
