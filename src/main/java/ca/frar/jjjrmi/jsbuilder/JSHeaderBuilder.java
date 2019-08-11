package ca.frar.jjjrmi.jsbuilder;

public class JSHeaderBuilder {
    private String simpleName = "";
    private String qualifiedName = "";
    private String extend = "";

    public JSHeaderBuilder setName(String name) {
        this.simpleName = name;
        return this;
    }

    public JSHeaderBuilder setExtend(String extend) {
        this.extend = extend;
        return this;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public String getExtend() {
        return extend;
    }

    public boolean hasExtend(){
        return !extend.isEmpty();
    }

    /**
    Output the string "class ${name} [extends classname]
    @param name
    @return
    @throws JSBuilderException
    */
    public String fullString(String name) throws JSBuilderException {
        StringBuilder builder = new StringBuilder();
        builder.append("class ");
        builder.append(name);
        if (!extend.isEmpty()){
            builder.append(" extends ");
            builder.append(getExtend());
        }
        return builder.toString();
    }
}
