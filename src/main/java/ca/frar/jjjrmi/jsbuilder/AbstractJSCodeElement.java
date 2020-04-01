/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.jsbuilder;
import ca.frar.jjjrmi.jsbuilder.code.CodeFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtTypeReference;

/**
 *
 * @author Ed Armstrong
 */
public class AbstractJSCodeElement implements JSCodeElement {
    ArrayList<JSCodeElement> childElements = new ArrayList<>();
    HashMap<String, Object> attributes = new HashMap<>();
    
    @Override
    public JSCodeElement get() {
        return this;
    }

    public boolean noChildren() {
        return countChildren() == 0;
    }
    
    public boolean hasChildren() {
        return countChildren() != 0;
    }    

    public String scoped() {
        return toString();
    }

    public int countChildren(){
        return childElements.size();
    }
    
    /**
     * Test child element against 'predicate'.
     * @param predicate
     * @return true if any child element passes predicate.
     */
    public boolean forAny(Predicate<JSCodeElement> predicate){
        for (JSCodeElement element : this.childElements){
            if (predicate.test(element)) return true;
        }
        return false;
    }    
    
    /**
     * Create a JSCode element from a CT element and attach the result as a
     * child element of this.
     *
     * @param ctCodeElement
     * @return
     */
    public JSCodeElement generate(CtElement ctCodeElement) {
        JSCodeElement generated = CodeFactory.generate(ctCodeElement).get();
        this.childElements.add(generated);
        return generated;
    }

    public JSElementList generateList() {
        return new JSElementList();
    }
    
    public JSElementList generateList(List<? extends CtCodeElement> ctCodeElements) {        
        JSElementList jsElementList = new JSElementList();
        
        for (CtCodeElement ctCodeElement : ctCodeElements){
            jsElementList.addCtCodeElement(ctCodeElement);
        }

        this.childElements.add(jsElementList);
        return jsElementList;
    }    
    
    /**
     * Retrieve all js 'requires' from this and all child elements of this.
     *
     * @return
     */
    @Override
    public Set<CtTypeReference> getRequires() {
        HashSet<CtTypeReference> requires = new HashSet<>();

        for (JSCodeElement element : this.childElements) {
            requires.addAll(element.getRequires());
        }

        return requires;
    }

    public String toXML(int indent) {
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
            builder.append("<").append(this.getClass().getSimpleName());
            for (String key : attributes.keySet()) {
                builder.append(" ").append(key).append("=\"").append(attributes.get(key)).append("\"");
            }
            builder.append("/>\n");
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

    @Override
    public void setAttr(String key, Object value) {
        this.attributes.put(key, value);
    }

    @Override
    public <T> T getAttr(String key) {
        return (T)this.attributes.get(key);
    }

    @Override
    public boolean hasAttr(String key) {
        return this.attributes.containsKey(key);
    }
}