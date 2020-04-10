package ca.frar.jjjrmi.jsbuilder;

import ca.frar.jjjrmi.jsbuilder.code.JSParameter;
import ca.frar.jjjrmi.Global;

import static ca.frar.jjjrmi.Global.LOGGER;
import static ca.frar.jjjrmi.Global.VERY_VERBOSE;
import ca.frar.jjjrmi.exceptions.UnknownParameterException;
import ca.frar.jjjrmi.exceptions.TypeDeclarationNotFoundWarning;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Set;
import spoon.reflect.reference.CtTypeReference;

/**
 * A JS Method, does not do the parsing.
 *
 * @author Ed Armstrong
 */
public class JSMethodBuilder {
    private String name = "";
    private final LinkedHashMap<String, JSParameter> parameters = new LinkedHashMap<>();
    private JSElementList body = new JSElementList();
    private boolean isAsync = false;
    private boolean isStatic = false;
    private boolean isSetter;
    private boolean isGetter;

    public JSMethodBuilder(String name) {
        LOGGER.log(VERY_VERBOSE, Global.line( "building method: " + name));;
        this.name = name;
    }

    public JSMethodBuilder() {
    }



    /**
     * Retrieve all js 'requires' from and all methods.
     *
     * @return
     */
    public Set<CtTypeReference> getRequires() {
        HashSet<CtTypeReference> set = new HashSet<>();

        try {
            set.addAll(this.body.getRequires());
        } catch (TypeDeclarationNotFoundWarning ex) {
            ex.setMethod(this.name);
            throw ex;
        }
        return set;
    }

    void setStatic(boolean b) {
        this.isStatic = true;
    }

    public JSMethodBuilder setAsync(boolean async) {
        this.isAsync = async;
        return this;
    }

    /**
     * Replace or add parameter to this method, with values set from
     * 'annotation'. The order of parameters is not affected if the parameter is
     * already set.
     */
    public JSMethodBuilder addParameter(JSParameter jsParameter) {
        LOGGER.log(VERY_VERBOSE, Global.line( "setting initialized parameter: " + jsParameter.getName() + " = " + jsParameter.getInitializer()));;
        parameters.put(jsParameter.getName(), jsParameter);
        return this;
    }

    /**
     * If this method does not already contain parameter 'name', add a new empty
     * parameter named 'name' to this method. The parameter is appended to the
     * end of the parameter list.
     *
     * @param name
     * @return
     */
    public JSMethodBuilder addParameter(String name) {
        LOGGER.log(VERY_VERBOSE, Global.line( "setting blank parameter: " + name));;
        JSParameter jsParameter = new JSParameter(name, "");
        parameters.put(name, jsParameter);
        return this;
    }

    /**
     * Return the initializer associated with parameter 'name'.
     *
     * @param name
     * @return
     */
    public JSParameter getParameter(String name) throws UnknownParameterException {
        if (!this.parameters.containsKey(name)) {
            throw new UnknownParameterException(name);
        }
        return this.parameters.get(name);
    }

    public JSMethodBuilder setBody(JSElementList body) {
        this.body = body;
        return this;
    }
    
    /**
     * Return the body of the element.  Any changes made to the returned body
     * will be reflected in the method.
     * @return 
     */
    public JSElementList getBody(){
        return this.body;
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
        
        Iterator<String> iterator = this.parameters.keySet().iterator();
        while(iterator.hasNext()){
            JSParameter jsParameter = this.parameters.get(iterator.next());
            builder.append(jsParameter.toString());
            if (iterator.hasNext()) builder.append(", ");
        }

        builder.append(") ");
        builder.append(JSFormatter.process(body.scoped(), 2).trim());

        return builder.toString();
    }

    void setSetter(boolean b) {
        this.isSetter = b;
    }

    void setGetter(boolean b) {
        this.isGetter = b;
    }

    public String toXML(int indent) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < indent; i++) builder.append("\t");
                
        builder.append("<").append(this.getClass().getSimpleName());
        builder.append(" name=\"").append(this.getName()).append("\"");
        builder.append(">\n");

        builder.append(parametersToXML(indent + 1));        
        
        builder.append(this.body.toXML(indent + 1));

        for (int i = 0; i < indent; i++) builder.append("\t");
        builder.append("</").append(this.getClass().getSimpleName()).append(">\n");

        return builder.toString();
    }

    public String parametersToXML(int indent) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < indent; i++) builder.append("\t");

        builder.append("<parameters>\n");

        for (String name : this.parameters.keySet()) {
            JSParameter jsParameter = this.parameters.get(name);
            builder.append(jsParameter.toXML(indent + 1));
        }

        for (int i = 0; i < indent; i++) builder.append("\t");
        builder.append("</parameters>\n");

        return builder.toString();
    }

    @Override
    public int hashCode(){
        return this.name.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final JSMethodBuilder other = (JSMethodBuilder) obj;
        if (!Objects.equals(this.name, other.name)) return false;
        return true;
    }    
    
}
