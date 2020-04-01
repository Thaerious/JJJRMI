package ca.frar.jjjrmi.jsbuilder.code;

import ca.frar.jjjrmi.jsbuilder.AbstractJSCodeElement;
import static ca.frar.jjjrmi.jsbuilder.code.JSConstructorCall.LOGGER;
import spoon.reflect.code.CtContinue;

public class JSContinue extends AbstractJSCodeElement {

    public JSContinue(CtContinue ctContinue) {
        LOGGER.trace(this.getClass().getSimpleName());
    }

    @Override
    public String toString() {
        return "continue";
    }

}
