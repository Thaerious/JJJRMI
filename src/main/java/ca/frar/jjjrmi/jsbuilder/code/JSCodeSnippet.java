package ca.frar.jjjrmi.jsbuilder.code;

import ca.frar.jjjrmi.jsbuilder.AbstractJSCodeElement;
import static ca.frar.jjjrmi.Global.LOGGER;

public class JSCodeSnippet extends AbstractJSCodeElement {

    private final String string;

    public JSCodeSnippet(String string) {
        LOGGER.trace(this.getClass().getSimpleName());
        this.string = string;
    }

    public JSCodeSnippet(String string, Object... objects) {
        this.string = String.format(string, objects);
    }

    @Override
    public String toString() {
        return string;
    }

    @Override
    public String toXMLNested(int indent) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < indent; i++) builder.append("\t");
        builder.append(string);
        builder.append("\n");
        return builder.toString();
    }
}
