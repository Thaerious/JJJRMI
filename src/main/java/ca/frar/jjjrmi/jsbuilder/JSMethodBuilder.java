package ca.frar.jjjrmi.jsbuilder;

import ca.frar.jjjrmi.exceptions.TypeDeclarationNotFoundWarning;
import ca.frar.jjjrmi.jsbuilder.code.AbstractJSCodeElement;
import ca.frar.jjjrmi.jsbuilder.code.JSCodeElement;
import ca.frar.jjjrmi.jsbuilder.code.JSCodeSnippet;
import ca.frar.jjjrmi.jsbuilder.code.JSElementList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

/**
 * A JS Method, does not do the parsing.
 * @author Ed Armstrong
 */
public class JSMethodBuilder {
    private String name = "";
    private final List<JSParameter> parameters = new ArrayList<>();
    private JSElementList body = new JSElementList();
    private boolean isAsync = false;
    private boolean isStatic = false;
    private boolean invokeSuper = false;
    private boolean isSetter;
    private boolean isGetter;

    public JSMethodBuilder(String name) {
        this.name = name;
    }

    public JSMethodBuilder() {
    }

    /**
     * Retrieve all js 'requires' from and all methods.
     * @return 
     */
    public Set<CtTypeReference> getRequires() {
        HashSet<CtTypeReference>set = new HashSet<>();
        
        try{
            set.addAll(this.body.getRequires());
        } catch (TypeDeclarationNotFoundWarning ex){
            ex.setMethod(this.name);
            throw ex;
        }
        return set;
    }    
    
    void setInvokeSuper(boolean b) {
        this.invokeSuper = b;
    }
    
    void setStatic(boolean b) {
        this.isStatic = true;
    }

    public JSMethodBuilder setAsync(boolean async) {
        this.isAsync = async;
        return this;
    }

    public JSMethodBuilder addParameter(JSParameter parameter) {
        parameters.add(parameter);
        return this;
    }
    
    public JSMethodBuilder addParameter(String name) {
        parameters.add(new JSParameter(name, ""));
        return this;
    }    
    
    /**
     * Returns a reflective JSParameter.  Changes made to this parameter affect
     * the class output.
     * @param name
     * @return 
     */
    public JSParameter getParameter(String name){
        for (JSParameter p : this.parameters){
            if (p.name.equals(name)) return p;
        }
        return null;
    }

    public JSMethodBuilder setBody(JSElementList body) {
        this.body = body;
        return this;
    }
    
    public JSMethodBuilder setBody(String body) {
        this.body = new JSElementList();
        this.body.add(new JSCodeSnippet(body));
        return this;
    }
    
    public JSMethodBuilder appendToBody(String snippet) {
        this.body.add(new JSCodeSnippet(snippet));
        return this;
    }

    public JSMethodBuilder appendToBody(JSCodeElement element) {
        this.body.add(element);
        return this;
    }

    public JSMethodBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public String fullString() throws JSBuilderException {
        if (name.isEmpty()) throw new JSBuilderIncompleteObjectException("method", "name");

        StringBuilder builder = new StringBuilder();
        builder.append("\t");
        if (this.isStatic) builder.append("static ");
        if (this.isAsync) builder.append("async ");
        if (this.isSetter) builder.append("set ");
        if (this.isGetter) builder.append("get ");
        builder.append(name);
        builder.append("(");

        for (int i = 0; i < parameters.size(); i++) {
            builder.append(parameters.get(i));
            if (i != parameters.size() - 1) builder.append(", ");
        }

        builder.append(") ");

        if (this.invokeSuper) {
            builder.append("\t\tif (super.").append(name).append(" !== undefined)");
            builder.append("super.").append(name).append("(");
            for (int i = 0; i < parameters.size(); i++) {
                builder.append(parameters.get(i));
                if (i != parameters.size() - 1) builder.append(", ");
            }
            builder.append(");\n");
        }

        builder.append(JSFormatter.process(body.scoped(), 2).trim());

        return builder.toString();
    }

    void setSetter(boolean b) {
        this.isSetter = b;
    }

    void setGetter(boolean b) {
        this.isGetter = b;
    }

    public String toXML(int indent){        
        StringBuilder builder = new StringBuilder();        
        
            for (int i = 0; i < indent; i++) builder.append("\t");
            
            builder.append("<").append(this.getClass().getSimpleName());
            builder.append(" name=\"").append(this.getName()).append("\"");
            builder.append(">\n");   
            
            builder.append(this.body.toXML(indent + 1));
            
            for (int i = 0; i < indent; i++) builder.append("\t");
            builder.append("</").append(this.getClass().getSimpleName()).append(">\n");
        
        return builder.toString();
    }
    
    public String parametersToXML(int indent){        
        StringBuilder builder = new StringBuilder();        
        
            for (int i = 0; i < indent; i++) builder.append("\t");
            
            builder.append("<parameters>");
            
            for (JSCodeElement element : this.parameters){
                builder.append(element.toXML(indent + 1));
            }
            
            for (int i = 0; i < indent; i++) builder.append("\t");
            builder.append("</parameters>");
        
        return builder.toString();
    }    
    
}
