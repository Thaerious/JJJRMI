package ca.frar.jjjrmi.jsbuilder.code;

import ca.frar.jjjrmi.jsbuilder.AbstractJSCodeElement;
import static ca.frar.jjjrmi.Global.LOGGER;
import spoon.reflect.code.CtCatch;

public class JSCatch extends AbstractJSCodeElement {

    private final JSBlock body;
    private final String param;

    public JSCatch(CtCatch ctCatch) {
        LOGGER.trace(this.getClass().getSimpleName());
        body = (JSBlock) this.generate(ctCatch.getBody());
        param = ctCatch.getParameter().getReference().getSimpleName();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("catch(");
        builder.append(param);
        builder.append(")");
        builder.append(body.scoped());
        return builder.toString();
    }

}
