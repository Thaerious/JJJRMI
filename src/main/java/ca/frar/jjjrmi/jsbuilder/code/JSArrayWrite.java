package ca.frar.jjjrmi.jsbuilder.code;

import ca.frar.jjjrmi.jsbuilder.JSCodeElement;
import ca.frar.jjjrmi.jsbuilder.AbstractJSCodeElement;
import static ca.frar.jjjrmi.Global.LOGGER;
import spoon.reflect.code.CtArrayWrite;

public class JSArrayWrite extends AbstractJSCodeElement {

    private final JSCodeElement target;
    private final JSCodeElement index;

    public JSArrayWrite(CtArrayWrite ctArrayWrite) {
        LOGGER.trace(this.getClass().getSimpleName());
        target = this.generate(ctArrayWrite.getTarget());
        index = this.generate(ctArrayWrite.getIndexExpression());
    }

    @Override
    public String toString(){
        return target + "[" + index + "]";
    }

}
