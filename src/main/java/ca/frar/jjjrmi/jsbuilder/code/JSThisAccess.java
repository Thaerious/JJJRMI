package ca.frar.jjjrmi.jsbuilder.code;

import static ca.frar.jjjrmi.Global.LOGGER;
import java.util.HashMap;
import spoon.reflect.code.CtThisAccess;

public class JSThisAccess extends AbstractJSCodeElement {

    public JSThisAccess(CtThisAccess<?> ctThisAccess) {
        LOGGER.trace(this.getClass().getSimpleName());
    }

    @Override
    public String toString(){
        return "this";
    }
}
