/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.translator;

import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.IncompleteDecoderException;

/**
 *
 * @author Ed Armstrong
 */
interface IDecoder {
    Object getObject() throws IncompleteDecoderException;
    boolean isComplete();
    public boolean decode() throws DecoderException;
}
