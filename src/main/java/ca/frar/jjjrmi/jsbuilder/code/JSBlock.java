package ca.frar.jjjrmi.jsbuilder.code;
import ca.frar.jjjrmi.jsbuilder.AbstractJSCodeElement;
import ca.frar.jjjrmi.jsbuilder.JSElementList;
import static ca.frar.jjjrmi.Global.LOGGER;
import spoon.reflect.code.CtBlock;

public class JSBlock extends AbstractJSCodeElement {

    private final JSElementList body;

    public JSBlock(CtBlock ctBlock) {
        body = this.generateList(ctBlock.getStatements());
    }

    @Override
    public String toString() {
        return body.scoped();
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