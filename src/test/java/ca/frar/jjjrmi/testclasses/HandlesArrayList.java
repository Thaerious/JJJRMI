/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.testclasses;

import ca.frar.jjjrmi.annotations.Handles;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.JSRequire;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.translator.AHandler;
import ca.frar.jjjrmi.translator.TranslatorResult;
import java.util.ArrayList;

/**
 *
 * @author Ed Armstrong
 */
@Handles("java.util.ArrayList")
@JSRequire(name="ArrayList", value="../static-testclasses/ArrayList")
@JJJ(insertJJJMethods=false)
public class HandlesArrayList extends AHandler<ArrayList<?>> {

    @NativeJS
    public HandlesArrayList(TranslatorResult translatorResult) {
        super(translatorResult);
    }

    @NativeJS
    @Override
    public ArrayList<?> getInstance() {
        return new ArrayList<>();
    }

    @Override
    public void decode(ArrayList t) throws DecoderException {
        Object[] decoded = this.decodeObject(Object[].class, "list");
        for (Object obj : decoded) t.add(obj);
    }

    @NativeJS("decode")
    private void jsDecode(ArrayList t) {
        /*JS{    
            let decoded = this.decodeObject("list");
                for(let obj of decoded){
                    t.add(obj);
                }
            t.list = decoded;
        }*/
    }

    @NativeJS
    @Override
    public void encode(ArrayList<?> arrayList) throws JJJRMIException {
        Object[] toArray = arrayList.toArray();
        this.encodeField("list", toArray);
    }
}
