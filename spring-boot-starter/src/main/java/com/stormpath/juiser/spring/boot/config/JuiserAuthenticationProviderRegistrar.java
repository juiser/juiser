package com.stormpath.juiser.spring.boot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

/**
 * @since 0.1.0
 */
@Configuration
public class JuiserAuthenticationProviderRegistrar extends AbstractHttpConfigurer<JuiserAuthenticationProviderRegistrar, HttpSecurity> {

    @Autowired
    @Qualifier("juiserAuthenticationProvider")
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private AuthenticationProvider juiserAuthenticationProvider;

    @SuppressWarnings("Duplicates")
    @Override
    public void configure(HttpSecurity http) throws Exception {

        // autowire this bean
        ApplicationContext context = http.getSharedObject(ApplicationContext.class);
        context.getAutowireCapableBeanFactory().autowireBean(this);

        http.authenticationProvider(juiserAuthenticationProvider);
    }
}