/*
 * Copyright 2017 Les Hazlewood and the respective Juiser contributors
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

import io.jsonwebtoken.Claims;
import org.juiser.jwt.config.JwtConfig;
import org.juiser.model.User;
import org.juiser.servlet.GuestFallbackUserFactory;
import org.juiser.servlet.RequestGuestFactory;
import org.juiser.servlet.RequestHeaderUserFactory;
import org.juiser.spring.web.RequestContextUser;
import org.juiser.spring.web.SpringForwardedUserFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;

/**
 * @since 1.0.0
 */
@SuppressWarnings({"SpringAutowiredFieldsWarningInspection", "SpringJavaAutowiringInspection"})
@Configuration
@ConditionalOnProperty(name = {"juiser.enabled"}, matchIfMissing = true)
@ConditionalOnJuiserSpringSecurityDisabled
public class JuiserDefaultAutoConfiguration {

    @Autowired
    @Qualifier("juiserForwardedUserJwsClaimsExtractor")
    private Function<String, Claims> juiserForwardedUserJwsClaimsExtractor;

    @Autowired
    @Qualifier("juiserJwtClaimsUserFactory")
    private Function<Claims, User> juiserJwtClaimsUserFactory;

    @Bean
    @ConditionalOnMissingBean
    public ForwardedHeaderConfig<JwtConfig> forwardedHeaderConfig() {
        return new ForwardedHeaderConfig<>();
    }

    @Bean
    @ConditionalOnMissingBean
    public ForwardedUserFilterConfig juiserForwardedUserFilterConfig() {
        return new ForwardedUserFilterConfig();
    }

    @Bean
    @ConditionalOnMissingBean(name = "juiserJwsUserFactory")
    public Function<String, User> juiserJwsUserFactory() {
        return s -> {
            Claims claims = juiserForwardedUserJwsClaimsExtractor.apply(s);
            org.springframework.util.Assert.state(claims != null && !claims.isEmpty(),
                "juiserForwardedUserJwsClaimsExtractor cannot return null or empty Claims instances.");
            User user = juiserJwtClaimsUserFactory.apply(claims);
            org.springframework.util.Assert.state(user != null,
                "juiserJwtClaimsUserFactory cannot return null User instances.");
            return user;
        };
    }

    @Bean
    @ConditionalOnMissingBean(name = "juiserRequestHeaderUserFactory")
    public Function<HttpServletRequest, User> juiserRequestHeaderUserFactory() {
        return new RequestHeaderUserFactory(forwardedHeaderConfig().getName(), juiserJwsUserFactory());
    }

    @Bean
    @ConditionalOnMissingBean(name = "juiserGuestFactory")
    public Function<HttpServletRequest, User> juiserGuestFactory() {
        return new RequestGuestFactory();
    }

    @Bean
    @ConditionalOnMissingBean(name = "juiserRequestUserFactory")
    public Function<HttpServletRequest, User> juiserRequestUserFactory() {
        return new GuestFallbackUserFactory(juiserRequestHeaderUserFactory(), juiserGuestFactory());
    }

    @Bean
    @ConditionalOnMissingBean(name = "juiserForwardedUserFilter")
    public FilterRegistrationBean juiserForwardedUserFilter() {

        ForwardedUserFilterConfig cfg = juiserForwardedUserFilterConfig();

        Filter filter = new SpringForwardedUserFilter(forwardedHeaderConfig().getName(),
            juiserRequestUserFactory(),
            cfg.getRequestAttributeNames());

        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(filter);
        bean.setEnabled(cfg.isEnabled());
        bean.setMatchAfter(cfg.isMatchAfter());
        bean.setOrder(cfg.getOrder());

        Set<DispatcherType> dispatcherTypes = cfg.getDispatcherTypes();
        if (!CollectionUtils.isEmpty(dispatcherTypes)) {
            bean.setDispatcherTypes(EnumSet.copyOf(dispatcherTypes));
        }
        Set<String> set = cfg.getServletNames();
        if (!CollectionUtils.isEmpty(set)) {
            bean.setServletNames(set);
        }
        set = cfg.getUrlPatterns();
        if (!CollectionUtils.isEmpty(set)) {
            bean.setUrlPatterns(set);
        }

        return bean;
    }

    @Bean
    @ConditionalOnMissingBean
    public User juiserUser() {
        return new RequestContextUser();
    }
}
