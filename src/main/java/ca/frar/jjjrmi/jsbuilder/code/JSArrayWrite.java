package ca.frar.jjjrmi.jsbuilder.code;

import spoon.reflect.code.CtArrayWrite;

public class JSArrayWrite extends AbstractJSCodeElement {

    private final JSCodeElement target;
    private final JSCodeElement index;

    public JSArrayWrite(CtArrayWrite ctArrayWrite) {
        target = CodeFactory.generate(ctArrayWrite.getTarget());
        index = CodeFactory.generate(ctArrayWrite.getIndexExpression());
    }

    @Override
    public String toString(){
        return target + "[" + index + "]";
    }

}
