package ca.frar.jjjrmi.annotations;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
* This annotation is added by the scriber to indicate a class was generated and not to process it.
*/
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Generated {
}
