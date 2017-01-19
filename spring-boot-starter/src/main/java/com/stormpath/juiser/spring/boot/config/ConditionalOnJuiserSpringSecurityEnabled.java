package com.stormpath.juiser.spring.boot.config;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link Conditional} that only matches when Spring Security classes are on the classpath <em>AND</em> none of
 * "juiser.security.enabled", "security.basic.enabled", "management.security.enabled" properties are {@code false}.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(JuiserSpringSecurityCondition.class)
public @interface ConditionalOnJuiserSpringSecurityEnabled {
}
