/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.translator.encoder;

import ca.frar.jjjrmi.translator.Translator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An array (wrapped) of encoded objects. The first object is the target object,
 * all other object are object that have not been previously translated.
 *
 * @author Ed Armstrong
 */
public class EncodedResult extends JSONObject {

    private final JSONArray objects;
    private final Translator translator;

    public EncodedResult(Translator translator) {
        this.translator = translator;
        this.objects = new JSONArray();
        this.put("new-objects", this.objects);
    }

    public EncodedResult(Translator translator, String source) {
        super(source);
        this.translator = translator;
        this.objects = new JSONArray();
    }

    /**
     * @return the translator
     */
    public Translator getTranslator() {
        return translator;
    }

    public void setRoot(String rootKey){
        this.put("root-object", rootKey);
    }

    public void put(JSONObject JSONObject) {
        this.objects.put(JSONObject);
    }

    /**
     * Return a new non-reflective list of all json objects added with 'put'.
     *
     * @return
     */
    public List<JSONObject> getAllObjects() {
        LinkedList<JSONObject> list = new LinkedList<>();

        for (Object json : this.objects) {
            list.add((JSONObject) json);
        }

        return list;
    }

}
