package ca.frar.jjjrmi.jsbuilder;

import ca.frar.jjjrmi.jsbuilder.AbstractJSCodeElement;
import java.util.List;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtComment;

public class JSElementList extends AbstractJSCodeElement {

    JSElementList(){}
    
    public JSElementList(List<? extends CtCodeElement> ctList) {
        for (CtCodeElement element : ctList) {
            if (!element.getComments().isEmpty()) {
                for (CtComment comment : element.getComments()) {
                    this.addCtCodeElement(comment);
                }
            }
            this.addCtCodeElement(element);
        }
    }
    
    /**
     * Retrieve the index of the first element with class 'aClass'.  Returns
     * -1 if none found.
     * @param aClass
     * @return 
     */
    public int firstIndexOf(Class<? extends JSCodeElement> aClass){
        int i = 0;
        for (JSCodeElement element : this.childElements){
            if (element.getClass() == aClass) return i;
            i++;
        }
        return -1;
    }
    
    /**
     * Retrieve the index of the first element with class 'aClass' searching
     * inclusively from index 'from'.  Returns -1 if no element found.
     * @param aClass
     * @return 
     */
    public int nextIndexOf(int from, Class<? extends JSCodeElement> aClass){
        int i = from;
        for (JSCodeElement element : this.childElements){
            if (element.getClass() == aClass) return i;
            i++;
        }
        return -1;
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
    
    public void remove(int i) {
        this.childElements.remove(i);
    }
}
