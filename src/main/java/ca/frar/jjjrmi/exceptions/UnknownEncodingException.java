/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.exceptions;

import org.json.JSONObject;

/**
 *
 * @author Ed Armstrong
 */
public class UnknownEncodingException extends DecoderException{
    
    public UnknownEncodingException(JSONObject json){
        super(json.toString(2));
    }
    
}
