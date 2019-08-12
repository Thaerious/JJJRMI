package ca.frar.jjjrmi.jsbuilder.code;
import spoon.reflect.code.CtArrayRead;

public class JSArrayRead extends AbstractJSCodeElement {

    private final JSCodeElement target;
    private final JSCodeElement index;

    public JSArrayRead(CtArrayRead<?> ctArrayRead) {
        target = CodeFactory.generate(ctArrayRead.getTarget());
        index = CodeFactory.generate(ctArrayRead.getIndexExpression());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(target);
        builder.append("[");
        builder.append(index);
        builder.append("]");
        return builder.toString();
    }

}
