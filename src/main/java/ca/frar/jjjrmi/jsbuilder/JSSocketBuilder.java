/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.jsbuilder;

import ca.frar.stream.TemplateVariableReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import spoon.reflect.declaration.CtClass;

/**
 *
 * @author Ed Armstrong
 */
public class JSSocketBuilder extends JSClassBuilder {
    private final String packageName;

    /**
     *
     * @param ctClass
     * @param packageName
     */
    public JSSocketBuilder(CtClass ctClass, String packageName) {
        super(ctClass);
        this.packageName = packageName;
    }

    public String fullString() {
        try {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("socket.template");
            if (inputStream == null) throw new RuntimeException("socket.template not found");
            
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            TemplateVariableReader templateVariableReader = new TemplateVariableReader(inputStreamReader);
            
            /* set variables to be copied into package.json template */
            templateVariableReader.set("socketName", this.getSimpleName());
            templateVariableReader.set("packageName", packageName);
            
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(templateVariableReader);
            
            String line = reader.readLine();
            while (line != null) {
                builder.append(line).append("\n");
                line = reader.readLine();
            }
            
            return builder.toString();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
