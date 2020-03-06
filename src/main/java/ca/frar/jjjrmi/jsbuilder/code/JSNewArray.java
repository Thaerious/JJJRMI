package ca.frar.jjjrmi.jsbuilder.code;

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
        
        if (dimensionExpressions.size() == 0){
            this.arraySize = "0";
        }
        else{
            CtExpression<Integer> ctExpression = dimensionExpressions.get(0);
            this.arraySize = ctExpression.toString();
        }
        List<CtExpression<?>> elements = ctNewArray.getElements();
        if (elements != null){
            this.elements = this.generateList(elements);
        }
    }

    public String toString(){
        return "new Array(" + arraySize + ")";
        
//        if (this.elements.isEmpty() && this.arraySize > 0){
//            return "new Array(" + arraySize + ")";
//        } else {
//            return "[" + elements.inline() + "]";
//        }
    }
    
    @Override
    public String toXML(int indent) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("array_size", "" + this.arraySize);
        hashMap.put("element_count", "" + this.elements.size());
        return toXML(indent, hashMap);
    }
}
