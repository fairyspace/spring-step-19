package io.github.fairyspace.context.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Scope {
    String value() default "singleton";
}
