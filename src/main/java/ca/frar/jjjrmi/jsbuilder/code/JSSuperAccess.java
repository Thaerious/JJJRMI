package ca.frar.jjjrmi.jsbuilder.code;

import spoon.reflect.code.CtSuperAccess;

public class JSSuperAccess implements JSCodeElement {

    public JSSuperAccess(CtSuperAccess ctSuperAccess) {
    }

    @Override
    public String toString() {
        return "super";
    }
}