package main.top.kindless.search.annotation;

import java.lang.annotation.*;

/**
 * Annotate a class or method to indicate that the class or method is thread unsafe.
 * @since 1.0
 * @author kindless
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface UnSafe {
}
