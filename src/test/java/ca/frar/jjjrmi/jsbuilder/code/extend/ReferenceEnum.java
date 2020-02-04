package ca.frar.jjjrmi.jsbuilder.code.extend;

import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.socket.JJJObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ed Armstrong
 */
public class ReferenceEnum extends JJJObject{

    private MyEnum myEnum = MyEnum.A;

    private MyEnum getMyEnum() {
        return this.myEnum;
    }
    
    @NativeJS
    public boolean test(MyEnum myEnum){
        if (this.getMyEnum() == MyEnum.B){
            return true;
        }
        return false;
    }
    
}
