package ca.frar.jjjrmi.jsbuilder;

import ca.frar.jjjrmi.jsbuilder.code.JSCodeElement;
import ca.frar.jjjrmi.jsbuilder.code.JSCodeSnippet;
import java.util.ArrayList;
import java.util.List;

public class JSMethodBuilder {
    private String name = "";
    private final List<String> parameters = new ArrayList<>();
    private final List<JSCodeElement> elements = new ArrayList<>();
    private boolean async = false;
    private boolean isStatic = false;
    private boolean invokeSuper = false;

    public JSMethodBuilder(String name) {
        this.name = name;
    }

    public JSMethodBuilder() {
    }

    void setInvokeSuper(boolean b) {
        this.invokeSuper = b;
    }

    public boolean isStatic() {
        return this.isStatic;
    }
    
    void setStatic(boolean b) {
        this.isStatic = true;
    }

    public boolean isAsync() {
        return async;
    }

    public JSMethodBuilder setAsync(boolean async) {
        this.async = async;
        return this;
    }

    public JSMethodBuilder addParameter(String parameter) {
        parameters.add(parameter);
        return this;
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
        if (this.isStatic()) builder.append("static ");
        if (this.isAsync()) builder.append("async ");
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

}
