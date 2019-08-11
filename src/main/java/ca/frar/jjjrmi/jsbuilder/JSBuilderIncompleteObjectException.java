package ca.frar.jjjrmi.jsbuilder;

public class JSBuilderIncompleteObjectException extends JSBuilderException{

    public JSBuilderIncompleteObjectException(String type, String param) {
        super(type + " is missing " + param);
    }

}
