/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.testableclasses;

import ca.frar.jjjrmi.annotations.Handles;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.translator.encoder.AHandler;
import ca.frar.jjjrmi.translator.encoder.EncodedResult;

/**
 *
 * @author Ed Armstrong
 */
@Handles("ca.frar.jjjrmi.testableclasses.HasHandler")
public class IsHandler extends AHandler<HasHandler>{

    public IsHandler(EncodedResult encodedResult) {
        super(encodedResult);
    }

    @Override
    public HasHandler decode() throws DecoderException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void encode(HasHandler object) throws EncoderException {
        this.setField("a", object.x);
        this.setField("b", object.y);
    }
    
}
