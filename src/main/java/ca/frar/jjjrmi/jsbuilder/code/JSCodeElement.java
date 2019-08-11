package ca.frar.jjjrmi.jsbuilder.code;

public interface JSCodeElement {
    /* return true if there is nothing to print */
    default boolean isEmpty() {return false;}

    /* print the element in curly brackets if applicable */
    default String scoped() {return toString();}
}
