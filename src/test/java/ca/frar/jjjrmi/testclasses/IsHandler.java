/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.testclasses;

import static ca.frar.jjjrmi.Global.LOGGER;
import ca.frar.jjjrmi.annotations.Handles;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.translator.encoder.AHandler;
import ca.frar.jjjrmi.translator.encoder.EncodedResult;

/**
 *
 * @author Ed Armstrong
 */
@Handles("ca.frar.jjjrmi.testclasses.HasHandler")
@JJJ
public class IsHandler extends AHandler<HasHandler>{

    public IsHandler(EncodedResult encodedResult) {
        super(encodedResult);
    }

    @NativeJS
    @Override
    public HasHandler getInstance() {
        return new HasHandler();
    }
    
    @NativeJS
    @Override
    public void decode(HasHandler hasHandler) throws DecoderException {
        this.decodeField("a", "x");
        this.decodeField("b", "y");        
        hasHandler.z = hasHandler.x + hasHandler.y;
        LOGGER.debug(hasHandler.x + ", " + hasHandler.y + ", " + hasHandler.z);
    }

    @NativeJS
    @Override
    public void encode(HasHandler object) throws EncoderException {
        this.encodeField("a", object.x);
        this.encodeField("b", object.y);
    }
}