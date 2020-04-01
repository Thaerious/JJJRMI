/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.jsbuilder.testclasses;

import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.rmi.socket.JJJObject;

/**
 *
 * @author Ed Armstrong
 */
public class ProgramLogic extends JJJObject{
    
    @NativeJS
    public int varArgList(int ... a){
        int s = 0;
        for (int i : a) s += i;
        return s;
    }
    
    @NativeJS
    public boolean statementIf1(int a){
        if (a % 2 == 0) return true;
        return false;
    }

    @NativeJS
    public boolean statementIf2(int a){
        if (a % 2 == 0){
            return true;
        } else {
            return false;
        }
    }

    @NativeJS
    public int statementIf3(int a){
        if (a % 2 == 0){
            int r = a * a;
            return r;
        } else if (a % 3 == 0){
            int r = a ^ a;
            return r;
        } else {
            return a;
        }
    }

    
    @NativeJS
    public boolean statementReturnEval(int a){
        return a % 2 == 0;
    }    
    
}
