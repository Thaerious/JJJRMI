/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.jsbuilder.code;

import ca.frar.jjjrmi.jsbuilder.AbstractJSCodeElement;
import java.util.Objects;
import spoon.reflect.declaration.CtParameter;

/**
 *
 * @author Ed Armstrong
 */
public class JSParameter extends AbstractJSCodeElement {

    public JSParameter(String name, String initializer) {
        if (initializer != null) this.setAttr("initializer", initializer);
        this.setAttr("name", name);
        this.setAttr("varargs", false);
    }

    public JSParameter(CtParameter<?> ctParameter) {
        this.setAttr("name", ctParameter.getSimpleName());
        this.setAttr("varargs", ctParameter.isVarArgs());
    }

    /**
     * Case insensitive comparison of two parameters by name.
     *
     * @param object
     * @return
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof JSParameter == false) return false;
        JSParameter that = (JSParameter) object;
        return this.getName().toLowerCase().equals(that.getName().toLowerCase());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.getName());
        return hash;
    }

    public String toString() {
        if (this.hasAttr("initializer") && !this.<String>getAttr("initializer").isEmpty()) {
            return getName() + " = " + getInitializer();
        } else {
            if ((boolean) this.getAttr("varargs")) {
                return "..." + getName();
            } else {
                return getName();
            }
        }
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.getAttr("name").toString();
    }

    /**
     * @return the initializer
     */
    public String getInitializer() {
        if (!this.hasAttr("initializer")) return "";
        return this.getAttr("initializer").toString();
    }

}
