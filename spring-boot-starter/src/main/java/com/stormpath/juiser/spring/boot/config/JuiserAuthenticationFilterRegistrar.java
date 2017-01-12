package com.stormpath.juiser.spring.boot.config;

import com.stormpath.juiser.spring.security.web.authentication.HeaderAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @since 0.1.0
 */
@Configuration
public class JuiserAuthenticationFilterRegistrar extends AbstractHttpConfigurer<JuiserAuthenticationFilterRegistrar, HttpSecurity> {

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private ForwardedHeaderConfig forwardedHeaderConfig;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public void init(HttpSecurity http) throws Exception {

        // autowire this bean
        ApplicationContext context = http.getSharedObject(ApplicationContext.class);
        context.getAutowireCapableBeanFactory().autowireBean(this);

        String headerName = forwardedHeaderConfig.getName();
        HeaderAuthenticationFilter filter = new HeaderAuthenticationFilter(headerName, authenticationManager);
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }
}
