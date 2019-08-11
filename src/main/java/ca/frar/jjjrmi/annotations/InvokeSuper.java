package ca.frar.jjjrmi.annotations;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Indicate that the generated JavaScript should check for and invoke the super method.
 * @author edward
 */
@Target(ElementType.METHOD)
public @interface InvokeSuper {
    
}
