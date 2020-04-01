package ca.frar.jjjrmi.jsbuilder.code;

import ca.frar.jjjrmi.jsbuilder.AbstractJSCodeElement;
import static ca.frar.jjjrmi.Global.LOGGER;
import spoon.reflect.code.CtSuperAccess;

public class JSSuperAccess extends AbstractJSCodeElement {

    public JSSuperAccess(CtSuperAccess ctSuperAccess) {
        LOGGER.trace(this.getClass().getSimpleName());
    }

    @Override
    public String toString() {
        return "super";
    }
}