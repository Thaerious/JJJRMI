package ca.frar.jjjrmi.jsbuilder;

import ca.frar.jjjrmi.jsbuilder.code.JSCodeElement;
import ca.frar.jjjrmi.jsbuilder.code.JSCodeSnippet;
import java.util.ArrayList;
import java.util.List;

public class JSMethodBuilder {
    private String name = "";
    private final List<JSParameter> parameters = new ArrayList<>();
    private final List<JSCodeElement> elements = new ArrayList<>();
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

    public JSMethodBuilder setBody(String body) {
        elements.clear();
        elements.add(new JSCodeSnippet(body));
        return this;
    }

    public JSMethodBuilder appendBody(String body) {
        elements.add(new JSCodeSnippet(body));
        return this;
    }

    public JSMethodBuilder appendBody(JSCodeElement element) {
        elements.add(element);
        return this;
    }

    public JSMethodBuilder prependBody(JSCodeElement element) {
        elements.add(0, element);
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

        builder.append(") {\n");

        if (this.invokeSuper) {
            builder.append("\t\tif (super.").append(name).append(" !== undefined)");
            builder.append("super.").append(name).append("(");
            for (int i = 0; i < parameters.size(); i++) {
                builder.append(parameters.get(i));
                if (i != parameters.size() - 1) builder.append(", ");
            }
            builder.append(");\n");
        }

        for (JSCodeElement element : elements) {
            builder.append(JSFormatter.process(element.toString(), 2));
        }

        builder.append("\t}");
        return builder.toString();
    }

    void setSetter(boolean b) {
        this.isSetter = b;
    }

    void setGetter(boolean b) {
        this.isGetter = b;
    }

}
