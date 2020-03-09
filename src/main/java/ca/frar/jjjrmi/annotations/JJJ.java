package ca.frar.jjjrmi.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation indicates that this class will be communicated between Java
 * server and JS client.
 *
 * @author edward
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface JJJ {

    /**
     * The resulting class name, if blank Java class name is used.
     *
     * @return
     */
    public String name() default "";

    /**
     * Add "extends [jsExtends] to the JS class, if blank ignored.
     *
     * @return
     */
    public String jsExtends() default "";

        /**
     * When true the client and server will retain a copy of instances of
     * this class. On subsequent sends the copy will be used. When set to false
     * the whole instance is encoded and sent every time.  Default value is true.
     * 
     * @return true, if object is to be tracked.
     */
    boolean retain() default true;

    /**
     * Set to false to skip generating javascript.
     *
     * @return
     */
    boolean generateJS() default true;
    
    /**
     * Process only the top level class
     * @return 
     */
    boolean topLevel() default false;
    
    /**
     * Add the __isTransient, __getCLass, and __isEnum methods.
     * @return 
     */
    boolean insertJJJMethods() default true;
}
