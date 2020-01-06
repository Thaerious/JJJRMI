package ca.frar.jjjrmi.annotations;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface NativeJS {

    /**
    Set this value to change the method name.
    @return
    */
    public String value() default "";
    public boolean isAsync() default false;
    public boolean isStatic() default false;
    public boolean isSetter() default false;
    public boolean isGetter() default false;
    public boolean callSuper() default true; // only valid on constructors
}
