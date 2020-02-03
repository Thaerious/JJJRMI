/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.exceptions;
import spoon.reflect.reference.CtTypeReference;

/**
 *
 * @author Ed Armstrong
 */
public class TypeDeclarationNotFoundWarning extends JJJRMIWarning{
    private String methodName = "";
    private String className = "";
    private final CtTypeReference<?> type;
    
    public TypeDeclarationNotFoundWarning(CtTypeReference<?> type) {
        super();
        this.type = type;
    }

    @Override
    public String getMessage(){
        StringBuilder builder = new StringBuilder();
        builder.append("Type declaration not found for '").append(type.getSimpleName()).append("' in ");
        if (!className.isEmpty()) builder.append(className);
        if (!methodName.isEmpty()) builder.append(".").append(methodName);
        if (className.isEmpty() && methodName.isEmpty()) builder.append("unknown class");
        return builder.toString();
    }
    
    public void setMethod(String name) {
        this.methodName = name;
    }

    public void setClass(String qualifiedName) {
        this.className = qualifiedName;
    }
}
