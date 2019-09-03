/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.jsbuilder;

import spoon.reflect.declaration.CtClass;

/**
 *
 * @author edward
 */
public class JSClassBuildException extends RuntimeException {

    JSClassBuildException(Exception cause, CtClass<?> ctClass) {
        super("JSClassBuilder build failed on class '" + ctClass.getQualifiedName() + "'", cause);
    }
    
}
