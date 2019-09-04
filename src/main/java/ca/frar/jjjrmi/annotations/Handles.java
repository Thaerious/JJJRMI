package ca.frar.jjjrmi.annotations;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
* Annotation indicating this class replaces encode/decode functionality.
* The value is the fully qualified class name of the class that this handler 
* encodes/decodes for.
*/
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Handles {
    String value();
}
