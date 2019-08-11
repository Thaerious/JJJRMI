package ca.frar.jjjrmi.jsbuilder.code;

import spoon.reflect.code.CtComment;

public class JSComment implements JSCodeElement {

    private String outputString;

    public JSComment(CtComment ctComment) {
        String content = ctComment.getContent().trim();
        if (content.startsWith("JS{") && content.endsWith("}")){
            int endOffset = 1;
            if (content.charAt(content.length() - 1) == ';') endOffset = 2;
            
            this.outputString = content.substring(3, content.length() - 1);
        } else {
            this.outputString = "/* " + content + " */";
        }
    }

    @Override
    public String toString() {
        return outputString;
    }

}
