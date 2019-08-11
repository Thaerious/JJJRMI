package ca.frar.jjjrmi.jsbuilder.code;
import spoon.reflect.code.CtBlock;

public class JSBlock implements JSCodeElement {

    private final JSElementList body;

    public JSBlock(CtBlock ctBlock) {
        body = new JSElementList(ctBlock.getStatements());
    }

    @Override
    public String toString() {
        return body.toString();
    }

    public String inline() {
        return body.inline();
    }

    public String scoped() {
        return body.scoped();
    }

    public String unscoped() {
        return body.unscoped();
    }
}