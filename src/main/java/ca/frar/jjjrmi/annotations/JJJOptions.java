package ca.frar.jjjrmi.annotations;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
Use this annotation to change default JJJ transcriber behavior.  This annotation is not required if all defaults are
used.
@author edward
*/
@Retention(value = RetentionPolicy.RUNTIME)
public @interface JJJOptions {

    /**
    Add "extends [jsExtends] to the JS class, if blank ignored.
    @return
    */
    public String jsExtends() default "";

    /**
    Indicates which methods will be translated into JS code.
    <ul>
    <li>NONE,       only those marked with NativeJS & !SkipJS
    <li>ANNOTATED,  only those marked with (NativeJS | ServerSide) & !SkipJS (default)
    <li>ALL         only those not marked with SkipJS
    </ul>
    @return
    */
    public ProcessLevel processLevel() default ProcessLevel.ANNOTATED;

    /**
    When set to true the client and server will retain a copy of objects of this class.  On subsequent sends the copy
    will be used.  When set to false the whole object is read and sent every time.
    @return
    */
    boolean retain() default true;

    /**
    Set to false to skip generating javascript.
    @return
    */
    boolean generateJS() default true;
}
