package ca.frar.jjjrmi.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
public @interface JSPrequels {
    JSPrequel[] value();
}