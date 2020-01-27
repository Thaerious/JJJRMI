package ca.frar.jjjrmi.jsbuilder.code;

import spoon.reflect.code.CtExpression;
import spoon.support.reflect.code.CtReturnImpl;

public class JSReturnImpl extends AbstractJSCodeElement {
    private final JSCodeElement ex;

    public JSReturnImpl(CtReturnImpl ctReturnImpl) {
        ex = this.generate(ctReturnImpl.getReturnedExpression());
    }

    @Override
    public String toString(){
        return "return " + ex.toString();
    }

}
