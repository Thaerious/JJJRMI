package ca.frar.jjjrmi.jsbuilder.code;

import ca.frar.jjjrmi.jsbuilder.AbstractJSCodeElement;
import static ca.frar.jjjrmi.Global.LOGGER;
import spoon.reflect.code.CtComment;

public class JSComment extends AbstractJSCodeElement {

    private String outputString;

    public JSComment(CtComment ctComment) {
        LOGGER.trace(this.getClass().getSimpleName());
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
