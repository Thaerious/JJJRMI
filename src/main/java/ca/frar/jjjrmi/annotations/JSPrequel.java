package ca.frar.jjjrmi.annotations;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
@Repeatable(JSPrequels.class)
public @interface JSPrequel {
    String value();
}