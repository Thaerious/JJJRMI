package ca.frar.jjjrmi.jsbuilder.code;

import ca.frar.jjjrmi.jsbuilder.JSCodeElement;
import ca.frar.jjjrmi.jsbuilder.AbstractJSCodeElement;
import ca.frar.jjjrmi.jsbuilder.JSElementList;
import static ca.frar.jjjrmi.Global.LOGGER;
import spoon.reflect.code.CtSwitch;

public class JSSwitch extends AbstractJSCodeElement {

    private final JSCodeElement selector;
    private final JSElementList cases;

    public JSSwitch(CtSwitch ctSwitch) {
        LOGGER.trace(this.getClass().getSimpleName());
        selector = this.generate(ctSwitch.getSelector());
        cases = this.generateList(ctSwitch.getCases());
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("switch (");
        builder.append(selector);
        builder.append(")");
        builder.append(cases.scoped());
        return builder.toString();
    }

}
