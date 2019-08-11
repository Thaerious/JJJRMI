package ca.frar.jjjrmi.jsbuilder.code;

import spoon.reflect.code.CtAssignment;

public class JSAssignment implements JSCodeElement {

    private final JSCodeElement rhs;
    private final JSCodeElement lhs;

    public JSAssignment(CtAssignment ctAssignment) {
        lhs = CodeFactory.generate(ctAssignment.getAssigned());
        rhs = CodeFactory.generate(ctAssignment.getAssignment());
    }

    @Override
    public String toString(){
        return lhs.toString() + " = " + rhs.toString();
    }

}
