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
public @interface IsSocket {
}
