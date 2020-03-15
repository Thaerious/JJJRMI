package ca.frar.jjjrmi.jsbuilder.code;

import static ca.frar.jjjrmi.Global.LOGGER;
import spoon.reflect.code.CtTry;

public class JSTry extends AbstractJSCodeElement {
    private final JSCodeElement body;
    private final JSElementList catchers;
    private final JSCodeElement finalizer;

    public JSTry(CtTry ctTry) {
        LOGGER.trace(this.getClass().getSimpleName());
        body = this.generate(ctTry.getBody());
        catchers = new JSElementList(ctTry.getCatchers());
        finalizer = this.generate(ctTry.getFinalizer());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("try");
        builder.append(body.scoped());
        builder.append(catchers);
        if (!finalizer.noChildren()){
            builder.append("finally");
            builder.append(finalizer.scoped());
        }
        return builder.toString();
    }

}
