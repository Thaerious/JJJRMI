package ca.frar.jjjrmi.jsbuilder.code;

import ca.frar.jjjrmi.jsbuilder.JSBuilderException;
import java.util.List;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtNewArray;

public class JSNewArray implements JSCodeElement {

    private CtExpression<Integer> arraySize;

    public JSNewArray(CtNewArray ctNewArray) {
        List<CtExpression<Integer>> dimensionExpressions = ctNewArray.getDimensionExpressions();
        if (dimensionExpressions.size() != 1){
            throw new JSBuilderException("Only single dimensional arrays may be instantiated.");
        }
        this.arraySize = dimensionExpressions.get(0);
    }

    public String toString(){
        return "new Array(" + arraySize + ")";
    }

}
