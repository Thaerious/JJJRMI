package ca.frar.jjjrmi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates that this class will be communicated between Java
 * server and JS client.
 *
 * @author edward
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(JSParams.class)
public @interface JSParam {
    public String name();
    public String init();
}
