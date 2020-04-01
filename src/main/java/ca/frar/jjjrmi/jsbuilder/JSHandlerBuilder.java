/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.jsbuilder;

import ca.frar.jjjrmi.Global;
import static ca.frar.jjjrmi.Global.LOGGER;
import static ca.frar.jjjrmi.Global.VERY_VERBOSE;
import ca.frar.jjjrmi.annotations.Handles;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.jsbuilder.code.JSCodeSnippet;
import spoon.reflect.declaration.CtClass;

/**
 * This builder is invoked for classes that directly extend AHandler.
 * @author Ed Armstrong
 */
public class JSHandlerBuilder<T> extends JSClassBuilder<T>{
    
    public JSHandlerBuilder(CtClass<T> ctClass) {
        super(ctClass);
    }
    
    JSClassBuilder<T> build() {
        LOGGER.log(VERY_VERBOSE, Global.line("Building AHandler"));
        
        Handles handles = ctClass.getAnnotation(Handles.class);
        if (handles == null) throw new NullPointerException("@Handles missing in: " + this.ctClass.getQualifiedName());
        
        getHeader().setExtend("AHandler");
        addRequire("AHandler", "jjjrmi", "AHandler");
        
        super.build();        
        
        JSMethodBuilder jsIsHandlerMethod = new JSMethodBuilder();
        jsIsHandlerMethod.setStatic(true);
        jsIsHandlerMethod.setName("__isHandler");
        jsIsHandlerMethod.getBody().add(new JSCodeSnippet("return true;"));
        addMethod(jsIsHandlerMethod);         
        
        JSMethodBuilder jsGetHandlesMethod = new JSMethodBuilder();
        jsGetHandlesMethod.setStatic(true);
        jsGetHandlesMethod.setName("__getHandles");
        String string = String.format("return '%s';", handles.value());
        jsGetHandlesMethod.getBody().add(new JSCodeSnippet(string));
        addMethod(jsGetHandlesMethod);            
        
        return this;
    }
    
}
