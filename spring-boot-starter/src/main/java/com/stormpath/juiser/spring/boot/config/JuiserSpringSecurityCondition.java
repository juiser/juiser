package com.stormpath.juiser.spring.boot.config;

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class JuiserSpringSecurityCondition extends SpringBootCondition {

    private static final Set<String> props = new LinkedHashSet<>(Arrays.asList("juiser.security.enabled", "security.basic.enabled", "management.security.enabled"));

    //This class is only available in Spring Security 4.2 or later, which is what we want since juiser-spring-security support requires 4.2+:
    private static final String SPRING_SEC_CLASS_NAME = "org.springframework.security.web.authentication.preauth.RequestAttributeAuthenticationFilter";

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {

        ConditionMessage matchMessage = ConditionMessage.empty();

        boolean enabled = isSpringSecurityEnabled(context);

        if (metadata.isAnnotated(ConditionalOnJuiserSpringSecurityEnabled.class.getName())) {
            if (!enabled) {
                return ConditionOutcome.noMatch(
                    ConditionMessage.forCondition(ConditionalOnJuiserSpringSecurityEnabled.class)
                        .didNotFind("spring security enabled").atAll());
            }
            matchMessage = matchMessage.andCondition(ConditionalOnJuiserSpringSecurityEnabled.class)
                .foundExactly("spring security enabled");
        }

        if (metadata.isAnnotated(ConditionalOnJuiserSpringSecurityDisabled.class.getName())) {
            if (enabled) {
                return ConditionOutcome.noMatch(
                    ConditionMessage.forCondition(ConditionalOnJuiserSpringSecurityDisabled.class)
                        .didNotFind("spring security disabled").atAll());
            }
            matchMessage = matchMessage.andCondition(ConditionalOnJuiserSpringSecurityDisabled.class)
                .didNotFind("spring security disabled").atAll();
        }

        return ConditionOutcome.match(matchMessage);
    }

    private boolean isSpringSecurityEnabled(ConditionContext ctx) {

        boolean enabled = true;

        Environment env = ctx.getEnvironment();

        for (String propName : props) {
            if (env.containsProperty(propName)) {
                if (!Boolean.parseBoolean(env.getProperty(propName))) {
                    enabled = false;
                    break;
                }
            }
        }

        if (enabled) {
            enabled = ClassUtils.isPresent(SPRING_SEC_CLASS_NAME, ctx.getClassLoader());
        }

        return enabled;
    }
}
