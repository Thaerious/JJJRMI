package ca.frar.jjjrmi.jsbuilder.testclasses;
import ca.frar.jjjrmi.annotations.DoNotInvoke;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.annotations.Transient;
import ca.frar.jjjrmi.rmi.socket.JJJObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class which will notify any subscribers of a changed value.
 * @author Ed Armstrong
 */
public class ModelElement extends JJJObject  implements Serializable{        
    @Transient private static final long serialVersionUID = 1L;
    @Transient final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(ModelElement.class);
    @Transient HashMap<String, ArrayList<ModelSubscriber>> subscribers = new HashMap<>();
    
    @NativeJS
    public ModelElement(){
        subscribers = new HashMap<>();
    }
    
    /**
     * Add a new subscriber to the this notifier.
     * @param subscriber new subscriber to add
     */
    @NativeJS
    public void addModelSubscriber(String eventName, ModelSubscriber subscriber){
        if (!this.subscribers.containsKey(eventName)){
            /* (1) */
            int a = 0;
            this.subscribers.put(eventName, new ArrayList<>());
        }
        ArrayList<ModelSubscriber> list = this.subscribers.get(eventName);
        list.add(subscriber);
    }
    
    @NativeJS
    public String[] domSubscribers(){
        return new String[0];
    }
    
    /**
     * Notify all subscribers of the change in value.
     * Use to notify server side subscribers of a model update.
     * @param name the property name
     * @param value the property value
     */
    @DoNotInvoke
    public void notifyModelSubscribers(String eventName, Object ... args){        
        super.invokeClientMethod("notifyModelSubscribers", eventName, args);
        if (!this.subscribers.containsKey(eventName)){
            this.subscribers.put(eventName, new ArrayList<>());
        }
        ArrayList<ModelSubscriber> list = this.subscribers.get(eventName);                
        
        for (ModelSubscriber subscriber : list){
            subscriber.notify(args);
        }
    }
    
    @NativeJS("notifyModelSubscribers")
    private void _jsNotifyModelSubscribers(String eventName, Object ... args){
        /*JS{
            if (!this.subscribers.containsKey(eventName))this.subscribers.put(eventName, new ArrayList());
            let list = this.subscribers.get(eventName);
                
            for(let subscriber of list){
                subscriber(...args);
            }
        
            for (let subscriber of this.domSubscribers()){
                let element = document.queryselector(`[model="${subscriber}"]`);
                if (typeof element[eventName] === "function"){
                    element[eventName](args);
                }
            }
        }*/
    }    
    
}