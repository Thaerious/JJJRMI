package ca.frar.jjjrmi.jsbuilder.code;

import ca.frar.jjjrmi.jsbuilder.AbstractJSCodeElement;
import ca.frar.jjjrmi.jsbuilder.JSElementList;
import static ca.frar.jjjrmi.Global.LOGGER;
import ca.frar.jjjrmi.jsbuilder.JSBuilderException;
import java.util.HashMap;
import java.util.List;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtNewArray;

public class JSNewArray extends AbstractJSCodeElement {
    private String arraySize;
    private JSElementList elements;

    public JSNewArray(CtNewArray<?> ctNewArray) {
        LOGGER.trace(this.getClass().getSimpleName());
        
        List<CtExpression<Integer>> dimensionExpressions = ctNewArray.getDimensionExpressions();
        if (dimensionExpressions.size() > 1){
            String msg = "Only single dimensional arrays may be instantiated: ";
            msg += dimensionExpressions.size() + " dimensions found.";
            throw new JSBuilderException(msg);
        }

        List<CtExpression<?>> elements = ctNewArray.getElements();
        if (elements != null){
            this.elements = this.generateList(elements);
        } else {
            this.elements = this.generateList();
        }
        
        if (!dimensionExpressions.isEmpty()){
            CtExpression<Integer> ctExpression = dimensionExpressions.get(0);
            this.arraySize = ctExpression.toString();
        }
        else if (this.elements.countChildren() == 0){
            this.arraySize = "0";
        } else {
            this.arraySize = "" + elements.size();
        }
    }

    public String toString(){        
        if (this.elements.countChildren() == 0){
            return "new Array(" + arraySize + ")";
        } else {
            return "[" + elements.inline() + "]";
        }
    }
    
    @Override
    public String toXML(int indent) {
        this.setAttr("array_size", "" + this.arraySize);
        this.setAttr("element_count", "" + this.elements.countChildren());
        return super.toXML(indent);
    }
}
