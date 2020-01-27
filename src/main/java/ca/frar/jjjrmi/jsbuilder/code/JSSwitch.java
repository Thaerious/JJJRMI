package ca.frar.jjjrmi.jsbuilder.code;

import spoon.reflect.code.CtSwitch;

public class JSSwitch extends AbstractJSCodeElement {

    private final JSCodeElement selector;
    private final JSElementList cases;

    public JSSwitch(CtSwitch ctSwitch) {
        selector = this.generate(ctSwitch.getSelector());
        cases = new JSElementList(ctSwitch.getCases());

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
