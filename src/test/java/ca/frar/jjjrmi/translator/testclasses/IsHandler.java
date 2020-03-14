/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.translator.testclasses;

import ca.frar.jjjrmi.annotations.Handles;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.jsbuilder.RequireRecord;
import ca.frar.jjjrmi.translator.AHandler;
import ca.frar.jjjrmi.translator.TranslatorResult;

/**
 *
 * @author Ed Armstrong
 */
@Handles("ca.frar.jjjrmi.translator.testclasses.HasHandler")
@JJJ
public class IsHandler extends AHandler<HasHandler>{

    @NativeJS
    public IsHandler(TranslatorResult encodedResult) {
        super(encodedResult);
    }

    @NativeJS
    @Override
    public HasHandler getInstance() {
        return new HasHandler();
    }
    
    @Override
    public void decode(HasHandler hasHandler) throws DecoderException {
        hasHandler.x = this.decodeObject(Integer.class, "a");
        hasHandler.y = this.decodeObject(Float.class, "b");        
        hasHandler.z = hasHandler.x + hasHandler.y;
    }

    @NativeJS("decode")
    public void jsdecode(HasHandler hasHandler) throws DecoderException {
        /*JS{
            hasHandler.x = this.decodeObject("a");
            hasHandler.y = this.decodeObject("b");
            hasHandler.z = hasHandler.x + hasHandler.y;
        }*/
    }    
    
    @NativeJS
    @Override
    public void encode(HasHandler object) throws JJJRMIException {
        this.encodeField("a", object.x);
        this.encodeField("b", object.y);
    }
    
    @Override
    public RequireRecord getRequire(){
        return new RequireRecord("HasHandler", "./HasHandler", "");
    }    
}