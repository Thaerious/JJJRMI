/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.testclasses;

/**
 *
 * @author edward
 */
public class Parent {
    Simple simple = new Simple();
    int[] array = {1, 2, 3};
    Simple other = this.simple;
    int[] otherArray = array;
    NoRetain temp = new NoRetain();
    Object object = null;
}
