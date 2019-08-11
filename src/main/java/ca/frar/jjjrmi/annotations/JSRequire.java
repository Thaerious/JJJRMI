package ca.frar.jjjrmi.annotations;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
@Repeatable(JSRequires.class)
public @interface JSRequire {
    String name();
    String value();
}