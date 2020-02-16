/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.translator;

import static ca.frar.jjjrmi.Global.LOGGER;
import static ca.frar.jjjrmi.Global.VERY_VERBOSE;
import ca.frar.jjjrmi.annotations.Handles;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

/**
 *
 * @author Ed Armstrong
 */
public class HandlerFactory {    
    private static HandlerFactory instance = null;
    private final HashMap<String, Class<? extends AHandler<?>>> classMap = new HashMap<>();
    
    public static HandlerFactory getInstance(){
        if (instance == null) instance = new HandlerFactory();
        return instance;
    }
    
    private HandlerFactory(){
        SubTypesScanner subTypesScanner = new SubTypesScanner(false);  
        TypeAnnotationsScanner typeAnnotationsScanner = new TypeAnnotationsScanner();
        Reflections reflections = new Reflections("", subTypesScanner, typeAnnotationsScanner);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Handles.class);           
        
        for (Class<?> aClass : classes){
            LOGGER.debug(aClass);
            Handles handles = aClass.getAnnotation(Handles.class);
            if (AHandler.class.isAssignableFrom(aClass)){
                LOGGER.log(VERY_VERBOSE, "adding handler " + aClass.getSimpleName() + " for class " + handles.value());
                classMap.put(handles.value(), (Class<? extends AHandler<?>>) aClass);
            }
        }
        LOGGER.debug("number of classes: " + classMap.size());
    }
    
    public boolean hasHandler(Class<?> aClass){
        if (aClass == null) throw new NullPointerException();
        return this.classMap.containsKey(aClass.getName());
    }

    public <T> Class<? extends AHandler<?>> getHandler(Class<T> aClass){
        return this.classMap.get(aClass.getName());
    }
    
    public List<Class<? extends AHandler<?>>> getAllHandlers(){
        Collection<Class<? extends AHandler<?>>> values = classMap.values();
        return List.copyOf(values);
    }
}
