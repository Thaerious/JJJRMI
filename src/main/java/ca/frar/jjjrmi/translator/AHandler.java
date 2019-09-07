/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.translator;

import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.EncoderException;
import org.json.JSONObject;

/**
 *
 * @author Ed Armstrong
 */
abstract public class AHandler<T> implements RestoreHandler, EncodeHandler{
    private final JSONObject json;
    private final Translator translator;
    private final JSONObject fields;

    public AHandler(JSONObject json, Translator translator){
        this.json = json;
        this.translator = translator;
        
        if (!this.json.has(Constants.FieldsParam)){
            this.json.put(Constants.FieldsParam, new JSONObject());
        }
        
        this.fields = json.getJSONObject(Constants.FieldsParam);
    }
    
    abstract public T instatiate();

    abstract public void jjjDecode(T object) throws DecoderException;

    abstract public void jjjEncode(T object) throws EncoderException;

    @Override
    public <T> T decodeField(String name) throws DecoderException {
        return (T) this.translator.decode(fields.getJSONObject(name));
    }

    @Override
    public void setField(String name, Object value) throws EncoderException {
        EncodedJSON toJSON = new Encoder(value, translator).encode();
        this.fields.put(name, toJSON);
    }
    
}
