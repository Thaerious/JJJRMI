package ca.frar.jjjrmi.jsbuilder.code;
import static ca.frar.jjjrmi.Global.LOGGER;
import spoon.reflect.code.CtBlock;

public class JSBlock extends AbstractJSCodeElement {

    private final JSElementList body;

    public JSBlock(CtBlock ctBlock) {
        LOGGER.trace(this.getClass().getSimpleName());
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