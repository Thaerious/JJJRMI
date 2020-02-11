/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.testclasses;

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
    public HasHandler getInstance() {
        return new HasHandler();
    }
    
    @Override
    public void decode(HasHandler hasHandler) throws DecoderException {
        hasHandler.x = this.decodeField("a", Integer.class);
        hasHandler.y = this.decodeField("b", Float.class);
        hasHandler.z = hasHandler.x + hasHandler.y;
    }

    @Override
    public void encode(HasHandler object) throws EncoderException {
        this.encodeField("a", object.x);
        this.encodeField("b", object.y);
    }
}