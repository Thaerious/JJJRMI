/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.translator.encoder;

import ca.frar.jjjrmi.annotations.Transient;
import ca.frar.jjjrmi.exceptions.EncoderException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.json.JSONObject;

/**
 *
 * @author Ed Armstrong
 */
public class EncodedFields extends JSONObject {
    private final Object parent;
    private final EncodedResult encodedResult;

    public EncodedFields(Object parent, EncodedResult encodedResult){
        this.parent = parent;
        this.encodedResult = encodedResult;
    }
    
    public void setField(Field field) throws EncoderException, IllegalArgumentException, IllegalAccessException {
        field.setAccessible(true);
        if (field.getAnnotation(Transient.class) != null) return;
        if (Modifier.isStatic(field.getModifiers())) return;        
        
        JSONObject toJSON = new Encoder(field.get(parent), this.encodedResult).encode();
        this.put(field.getName(), toJSON);
    }

}
