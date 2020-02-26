/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.jsbuilder;

import static ca.frar.jjjrmi.Global.LOGGER;
import static ca.frar.jjjrmi.Global.VERY_VERBOSE;
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
        LOGGER.log(VERY_VERBOSE, "Building AHandler");
        JSClassBuilder<T> builder = super.build();
        builder.getHeader().setExtend("AHandler");
        builder.addRequire("AHandler", "jjjrmi/translator/AHandler", "");

        return this;
    }
    
}
