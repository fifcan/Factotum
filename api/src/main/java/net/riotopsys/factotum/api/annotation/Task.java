package net.riotopsys.factotum.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by afitzgerald on 8/27/14.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Task {

    String DEFAULT = "";

    String requestName() default DEFAULT;

}
