package ca.frar.jjjrmi.annotations;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Indicates the method to be executed after decoding takes place.
 * If the method also has @NativeJS it will added to the JS class
 * as __afterDecode.
 * If @JJJ(insertJJJMethods=false) this will be skipped regardless.
 * as __afterDecode
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
public @interface AfterDecode {}
