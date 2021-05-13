package com.ayou.pulsardemo.infrastructure.annotation;

import org.springframework.messaging.handler.annotation.MessageMapping;

import java.lang.annotation.*;

/**
 * @author ysy
 * @blame ysy
 * @date 2020-01-11
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@MessageMapping
@Documented
@Repeatable(PulsarListeners.class)
public @interface PulsarListener {
    String topic() default "";

    String subscriptionName() default "";
}
