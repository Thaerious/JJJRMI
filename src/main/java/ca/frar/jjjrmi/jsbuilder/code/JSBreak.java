package ca.frar.jjjrmi.jsbuilder.code;

import spoon.reflect.code.CtBreak;

public class JSBreak implements JSCodeElement {

    public JSBreak(CtBreak ctBreak) {
    }

    @Override
    public String toString() {
        return "break";
    }

}
