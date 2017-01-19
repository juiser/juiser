/*
 * Copyright (C) 2017 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.juiser.spring.boot.config;

import org.juiser.spring.security.config.SpringSecurityJwtConfig;
import org.juiser.spring.security.web.authentication.HeaderAuthenticationFilter;
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
@SuppressWarnings({"SpringAutowiredFieldsWarningInspection", "SpringJavaAutowiringInspection"})
@Configuration
public class JuiserAuthenticationFilterRegistrar extends AbstractHttpConfigurer<JuiserAuthenticationFilterRegistrar, HttpSecurity> {

    @Autowired
    private ForwardedHeaderConfig forwardedHeaderConfig;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public void init(HttpSecurity http) throws Exception {

        // autowire this bean
        ApplicationContext context = http.getSharedObject(ApplicationContext.class);
        context.getAutowireCapableBeanFactory().autowireBean(this);

        boolean springSecurityEnabled = forwardedHeaderConfig.getJwt() instanceof SpringSecurityJwtConfig;

        if (springSecurityEnabled) {
            String headerName = forwardedHeaderConfig.getName();
            HeaderAuthenticationFilter filter = new HeaderAuthenticationFilter(headerName, authenticationManager);
            http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        } //else juiser.security.enabled is false or spring security is disabled via a property
    }
}
