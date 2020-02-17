package ca.frar.jjjrmi.annotations;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class will get generated, but will not get added to the package file.
 * @author Ed Armstrong
 */

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
public @interface DoNotPackage {}
