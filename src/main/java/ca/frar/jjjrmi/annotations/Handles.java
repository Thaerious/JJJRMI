package ca.frar.jjjrmi.annotations;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
* Annotation indicating this class replaces encode/decode functionality.
* The value is the fully qualified class name of the class that this handler 
* encodes/decodes for.
*/
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Handles {
    String value();
}
