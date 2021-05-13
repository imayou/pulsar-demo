package com.ayou.pulsardemo.infrastructure.annotation;

import java.lang.annotation.*;

/**
 * @author ysy
 * @blame ysy
 * @date 2020-01-11
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PulsarListeners {
    PulsarListener[] value();
}
