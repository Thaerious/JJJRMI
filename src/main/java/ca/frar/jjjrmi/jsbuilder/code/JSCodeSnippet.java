package ca.frar.jjjrmi.jsbuilder.code;

public class JSCodeSnippet implements JSCodeElement{
    private final String string;

    public JSCodeSnippet(String string){
        this.string = string;
    }

    public JSCodeSnippet(String string, Object ... objects){
        this.string = String.format(string, objects);
    }

    @Override
    public String toString(){
        return string;
    }
}