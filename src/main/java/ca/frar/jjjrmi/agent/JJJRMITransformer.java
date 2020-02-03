/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.agent;

import ca.frar.jjjrmi.annotations.Handles;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Ed Armstrong
 */
public class JJJRMITransformer implements ClassFileTransformer {
    final public static Map<Class<?>, String> handlerMap = new HashMap<>();
    
    public byte[] transform(ClassLoader loader, String className, Class<?> aClass, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (!className.startsWith("ca/frar")) return classfileBuffer;
        System.out.println("JJJRMITransformer agent " + className);
        
        Handles annotation = aClass.getAnnotation(Handles.class);
        if (annotation != null){            
            handlerMap.put(aClass, annotation.value());
        }
        
        return classfileBuffer;
    }
    
    public static HashMap<Class<?>, String> getHandlerMap(){
        return new HashMap<>(handlerMap);
    }
}
