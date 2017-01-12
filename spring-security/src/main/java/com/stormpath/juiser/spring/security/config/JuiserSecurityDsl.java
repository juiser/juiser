package com.stormpath.juiser.spring.security.config;

import com.stormpath.juiser.spring.security.web.authentication.HeaderAuthenticationFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;

/**
 * @since 0.1.0
 */
public class JuiserSecurityDsl extends AbstractHttpConfigurer<JuiserSecurityDsl, HttpSecurity> {

    @Override
    public void configure(HttpSecurity http) throws Exception {

        ApplicationContext context = http.getSharedObject(ApplicationContext.class);

        HeaderAuthenticationFilter filter = context.getBean(HeaderAuthenticationFilter.class);
        http.addFilterBefore(filter, DigestAuthenticationFilter.class);

        super.configure(http);
    }
}
