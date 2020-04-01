/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.jsbuilder.testclasses;

import ca.frar.jjjrmi.annotations.NativeJS;
import java.util.ArrayList;

/**
 *
 * @author Ed Armstrong
 */
public class ExtendSwitched extends Switched {

    @NativeJS
    public String[] domSubscribers(){
        return new String[]{"a", "b"};
    }
    
    @NativeJS
    public Cardinality cardinality(String target) {
        return Cardinality.N;
    }
}
