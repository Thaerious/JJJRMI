/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.translator;

import ca.frar.jjjrmi.translator.encoder.AHandler;
import ca.frar.jjjrmi.annotations.Handles;
import java.util.HashMap;
import java.util.Set;
import org.reflections.Reflections;

/**
 *
 * @author Ed Armstrong
 */
public class HandlerFactory {
    private static HandlerFactory instance = null;
    private final Reflections reflections;   
    private final HashMap<String, Class<? extends AHandler<?>>> classMap = new HashMap<>();
    
    public static HandlerFactory getInstance(){
        if (instance == null) instance = new HandlerFactory();
        return instance;
    }
    
    public HandlerFactory(){
        reflections = new Reflections("");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Handles.class);
        for (Class<?> aClass : annotated){
            Handles handles = aClass.getAnnotation(Handles.class);
            if (AHandler.class.isAssignableFrom(aClass)){
                classMap.put(handles.value(), (Class<? extends AHandler<?>>) aClass);
            }
        }
    }
    
    public boolean hasHandler(Class<?> aClass){
        return this.classMap.containsKey(aClass.getName());
    }

    public <T> Class<? extends AHandler<?>> getHandler(Class<T> aClass){
        return this.classMap.get(aClass.getName());
    }
}
