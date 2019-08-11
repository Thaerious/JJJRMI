package ca.frar.jjjrmi.jsbuilder.code;

import spoon.reflect.code.CtTry;

public class JSTry implements JSCodeElement {
    private final JSCodeElement body;
    private final JSElementList catchers;
    private final JSCodeElement finalizer;

    public JSTry(CtTry ctTry) {
        body = CodeFactory.generate(ctTry.getBody());
        catchers = new JSElementList(ctTry.getCatchers());
        finalizer = CodeFactory.generate(ctTry.getFinalizer());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("try");
        builder.append(body.scoped());
        builder.append(catchers);
        if (!finalizer.isEmpty()){
            builder.append("finally");
            builder.append(finalizer.scoped());
        }
        return builder.toString();
    }

}
