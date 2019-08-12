package ca.frar.jjjrmi.jsbuilder.code;

import spoon.reflect.code.CtThisAccess;

public class JSThisAccess extends AbstractJSCodeElement {

    public JSThisAccess(CtThisAccess<?> ctThisAccess) {
    }

    @Override
    public String toString(){
        return "this";
    }

}
