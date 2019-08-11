package ca.frar.jjjrmi.jsbuilder.code;

public class JSEmptyElement implements JSCodeElement {

    @Override
    public String toString() {
        return "";
    }

    public boolean isEmpty(){
        return true;
    }

}
