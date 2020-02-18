package ca.frar.jjjrmi.jsbuilder.code;

import static ca.frar.jjjrmi.jsbuilder.code.JSConstructorCall.LOGGER;
import java.util.ArrayList;
import java.util.List;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtComment;

public class JSElementList extends AbstractJSCodeElement {

    public JSElementList() {
        LOGGER.trace(this.getClass().getSimpleName());
    }

    public JSElementList(List<? extends CtCodeElement> ctList) {
        LOGGER.trace(this.getClass().getSimpleName());
        for (CtCodeElement element : ctList) {
            if (!element.getComments().isEmpty()) {
                for (CtComment comment : element.getComments()) {
                    this.addCtCodeElement(comment);
                }
            }
            this.addCtCodeElement(element);
        }
    }
    
    final public void addCtCodeElement(CtCodeElement element) {
        this.generate(element);
    }

    public String inline() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.childElements.size(); i++) {
            builder.append(this.childElements.get(i).toString());
            if (i != this.childElements.size() - 1) builder.append(", ");
        }
        return builder.toString();
    }

    /**
     * Return a string with enclosing brackets.
     *
     * @return
     */
    public String scoped() {
        StringBuilder builder = new StringBuilder();        

        if (this.childElements.isEmpty()) {
            builder.append("{}");
        } else {
            builder.append("{\n");
            for (int i = 0; i < this.childElements.size(); i++) {                
                JSCodeElement ele = this.childElements.get(i);
                String s = ele.toString();
                builder.append(s);
                if (s.trim().endsWith(";") || s.trim().endsWith("}")) builder.append("\n");
                else if (!s.isEmpty()) builder.append(";\n");
            }
            builder.append("}");
        }
        return builder.toString();
    }

    /**
     * Return a string without enclosing brackets.
     *
     * @return
     */
    public String unscoped() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.childElements.size(); i++) {
            JSCodeElement ele = this.childElements.get(i);
            String s = ele.toString();
            if (!s.isEmpty()) {
                builder.append(s);
                if (s.trim().endsWith(";") || s.trim().endsWith("}")) builder.append("\n");
                else builder.append(";\n");
            }
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        if (this.childElements.size() > 1) {
            return scoped();
        } else if (this.childElements.size() == 1) {
            return unscoped();
        }
        return builder.toString();
    }

    public void add(int i, JSCodeElement jsCodeElement) {
        this.childElements.add(i, jsCodeElement);
    }

    public void add(JSCodeElement jsCodeElement) {
        this.childElements.add(jsCodeElement);
    }
}
