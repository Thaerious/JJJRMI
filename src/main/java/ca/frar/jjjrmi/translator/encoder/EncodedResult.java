/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.translator.encoder;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.translator.Constants;
import ca.frar.jjjrmi.translator.Translator;
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
@JJJ(insertJJJMethods=false)
public class EncodedResult extends JSONObject {
    private final JSONObject objects;
    private final Translator translator;
    
    public EncodedResult(Translator translator) {
        this.translator = translator;
        this.objects = new JSONObject();
        this.put(Constants.NewObjects, this.objects);
    }

    @NativeJS
    public EncodedResult(Translator translator, String source) {
        super(source);
        this.translator = translator;
        this.objects = this.getJSONObject(Constants.NewObjects);
    }

    /**
     * @return the translator
     */
    @NativeJS
    public Translator getTranslator() {
        return translator;
    }

    @NativeJS
    public void setRoot(String rootKey){
        this.put(Constants.RootObject, rootKey);
    }

    @NativeJS
    public String getRoot(){
        return this.getString(Constants.RootObject);
    }    
    
    @NativeJS
    public void put(EncodedObject encodedObject) {
        this.objects.put(encodedObject.getString(Constants.KeyParam), encodedObject);
    }

    /**
     * Return a new non-reflective list of all json objects added with 'put'.
     *
     * @return
     */
    @NativeJS
    public List<JSONObject> getAllObjects() {
        LinkedList<JSONObject> list = new LinkedList<>();

        for (String key : this.objects.keySet()){
            list.add((JSONObject) this.objects.get(key));
        }

        return list;
    }

}
