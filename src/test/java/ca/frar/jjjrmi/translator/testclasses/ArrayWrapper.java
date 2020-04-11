/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.translator.testclasses;
import ca.frar.jjjrmi.annotations.JJJ;

/**
 *
 * @author edward
 */
@JJJ
public class ArrayWrapper {
    public int[] arrayField1 = {1, 1, 2, 3, 5};
    public int[] arrayField2 = new int[5];
    public Shapes[] arrayField3 = {Shapes.CIRCLE, Shapes.SQUARE, Shapes.SQUARE, Shapes.TRIANGLE};
}
