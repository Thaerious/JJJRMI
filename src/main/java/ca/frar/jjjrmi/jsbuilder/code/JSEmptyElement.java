package ca.frar.jjjrmi.jsbuilder.code;

import ca.frar.jjjrmi.jsbuilder.AbstractJSCodeElement;
import static ca.frar.jjjrmi.jsbuilder.code.JSConstructorCall.LOGGER;

public class JSEmptyElement extends AbstractJSCodeElement {

    public JSEmptyElement(){
        LOGGER.trace(this.getClass().getSimpleName());
    }
    
    @Override
    public String toString() {
        return "";
    }

    public boolean noChildren(){
        return true;
    }

}
