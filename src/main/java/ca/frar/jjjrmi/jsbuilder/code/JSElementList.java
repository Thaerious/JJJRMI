package ca.frar.jjjrmi.jsbuilder.code;
import java.util.ArrayList;
import java.util.List;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtComment;

public class JSElementList extends ArrayList<JSCodeElement> {

    public JSElementList() {
    }

    public JSElementList(List<? extends CtCodeElement> ctList) {
        for (CtCodeElement element : ctList) {
            if (!element.getComments().isEmpty()){
                for (CtComment comment : element.getComments()){
                    this.addCtCodeElement(comment);
                }
            }
            this.addCtCodeElement(element);
        }
    }

    final public void setCtCodeElement(int index, CtCodeElement element){
        this.set(index, CodeFactory.generate(element));
    }

    final public void addCtCodeElement(CtCodeElement element){
        this.add(CodeFactory.generate(element));
    }

    public String inline() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.size(); i++) {
            builder.append(get(i).toString());
            if (i != this.size() - 1) builder.append(", ");
        }
        return builder.toString();
    }

    public String scoped() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        for (int i = 0; i < this.size(); i++) {
            JSCodeElement ele = get(i);
            String s = ele.toString();
            builder.append(s);
            if (s.trim().endsWith(";") || s.trim().endsWith("}")) builder.append("\n");
            else builder.append(";\n");
        }
        builder.append("}");
        return builder.toString();
    }

    public String unscoped() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.size(); i++) {
            JSCodeElement ele = get(i);
            String s = ele.toString();
            if (!s.isEmpty()){
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

        if (this.size() > 1) {
            return scoped();
        } else if (this.size() == 1) {
            return unscoped();
        }
        return builder.toString();
    }
}
