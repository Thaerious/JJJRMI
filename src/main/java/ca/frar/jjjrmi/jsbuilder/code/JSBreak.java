package ca.frar.jjjrmi.jsbuilder.code;

import static ca.frar.jjjrmi.Global.LOGGER;
import spoon.reflect.code.CtBreak;

public class JSBreak extends AbstractJSCodeElement {

    public JSBreak(CtBreak ctBreak) {
        LOGGER.trace(this.getClass().getSimpleName());
    }

    @Override
    public String toString() {
        return "break";
    }

}
