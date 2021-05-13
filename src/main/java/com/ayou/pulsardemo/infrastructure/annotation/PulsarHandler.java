package com.ayou.pulsardemo.infrastructure.annotation;

import org.springframework.messaging.handler.annotation.MessageMapping;

import java.lang.annotation.*;

/**
 * PulsarHandler
 *
 * @author ysy
 * @blame ysy
 * @date 2020-01-13
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@MessageMapping
@Documented
public @interface PulsarHandler {
    /**
     * When true, designate that this is the default fallback method if the payload type
     * matches no other {@link PulsarHandler} method. Only one method can be so designated.
     *
     * @return true if this is the default method.
     * @since 2.1.3
     */
    boolean isDefault() default false;
}
