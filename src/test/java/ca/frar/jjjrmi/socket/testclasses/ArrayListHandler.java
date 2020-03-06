/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.socket.testclasses;

import ca.frar.jjjrmi.annotations.Handles;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.JSRequire;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.jsbuilder.RequireRecord;
import ca.frar.jjjrmi.translator.AHandler;
import ca.frar.jjjrmi.translator.TranslatorResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Ed Armstrong
 */
@Handles("java.util.ArrayList")
@JJJ(insertJJJMethods=false)
@JSRequire(name="ArrayList", value="../ext/ArrayList")
public class ArrayListHandler extends AHandler<ArrayList>{

    @NativeJS
    public ArrayListHandler(TranslatorResult tr) {
        super(tr);
    }

    @Override
    public void decode(ArrayList t) throws DecoderException {
        Object[] array = this.decodeObject(Object[].class, "array");
        List<Object> asList = Arrays.<Object>asList(array);
        t.addAll(asList);
    }

    @NativeJS("decode")
    private void jsDecode(ArrayList t){
        /*JS{
            let array = this.decodeObject("array");
            t.addAll(array);
        }*/
    }
    
    @Override
    @NativeJS
    public void encode(ArrayList t) throws JJJRMIException {
        int s = t.size();
        Object[] array = new Object[s];
        t.toArray(array);
        this.encodeField("array", array);
    }

    @Override
    @NativeJS
    public ArrayList getInstance() {
        return new ArrayList<>();
    }    
    
    @Override
    public RequireRecord getRequire(){
        return new RequireRecord("ArrayList", "../ext/ArrayList", "");
    }
}
