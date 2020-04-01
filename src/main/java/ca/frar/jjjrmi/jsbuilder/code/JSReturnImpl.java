package ca.frar.jjjrmi.jsbuilder.code;

import ca.frar.jjjrmi.jsbuilder.JSCodeElement;
import ca.frar.jjjrmi.jsbuilder.AbstractJSCodeElement;
import static ca.frar.jjjrmi.Global.LOGGER;
import spoon.reflect.code.CtExpression;
import spoon.support.reflect.code.CtReturnImpl;

public class JSReturnImpl extends AbstractJSCodeElement {
    private final JSCodeElement ex;

    public JSReturnImpl(CtReturnImpl ctReturnImpl) {
        LOGGER.trace(this.getClass().getSimpleName());
        ex = this.generate(ctReturnImpl.getReturnedExpression());
    }

    @Override
    public String toString(){
        return "return " + ex.toString();
    }

}
