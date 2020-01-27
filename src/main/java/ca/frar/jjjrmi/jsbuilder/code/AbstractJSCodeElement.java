/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.jsbuilder.code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtType;

/**
 *
 * @author Ed Armstrong
 */
public class AbstractJSCodeElement implements JSCodeElement {

    ArrayList<JSCodeElement> childElements = new ArrayList<>();

    public boolean isEmpty() {
        return false;
    }

    public String scoped() {
        return toString();
    }

    /**
     * Create a JSCode element from a CT element and attach the result as a
     * child element of this.
     *
     * @param ctCodeElement
     * @return
     */
    JSCodeElement generate(CtCodeElement ctCodeElement) {
        JSCodeElement generated = CodeFactory.generate(ctCodeElement);
        this.childElements.add(generated);
        return generated;
    }

    /**
     * Retrieve all js 'requires' from this and all child elements of this.
     *
     * @return
     */
    @Override
    public Set<CtType> getRequires() {
        HashSet<CtType> requires = new HashSet<>();

        for (JSCodeElement element : this.childElements) {
            requires.addAll(element.getRequires());
        }

        return requires;
    }

    public String toXML(int indent, HashMap<String, String> attributes) {
        StringBuilder builder = new StringBuilder();

        String nested = this.toXMLNested(indent + 1);
        
        if (nested != null && !nested.isEmpty()) {
            for (int i = 0; i < indent; i++) builder.append("\t");
            builder.append("<").append(this.getClass().getSimpleName());

            for (String key : attributes.keySet()) {
                builder.append(" ").append(key).append("=\"").append(attributes.get(key)).append("\"");
            }
            builder.append(">\n");

            builder.append(nested);
            
            for (int i = 0; i < indent; i++) builder.append("\t");
            builder.append("</").append(this.getClass().getSimpleName()).append(">\n");
        } else {
            for (int i = 0; i < indent; i++) builder.append("\t");
            builder.append("<").append(this.getClass().getSimpleName()).append("/>\n");
        }

        return builder.toString();
    }

    public String toXMLNested(int indent) {
        StringBuilder builder = new StringBuilder();
        for (JSCodeElement element : this.childElements) {
            builder.append(element.toXML(indent));
        }
        return builder.toString();
    }

    public String toXML(int indent) {
        return toXML(indent, new HashMap<>());
    }

}
