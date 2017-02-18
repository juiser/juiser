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

import org.juiser.jwt.StringCollectionClaimResolver;
import org.juiser.model.User;
import org.juiser.spring.jwt.ClaimsExpressionEvaluator;
import org.juiser.spring.security.authentication.ClaimsGrantedAuthoritiesResolver;
import org.juiser.spring.security.authentication.HeaderAuthenticationProvider;
import org.juiser.spring.security.authentication.JwsToUserDetailsConverter;
import org.juiser.spring.security.config.SpringSecurityJwtConfig;
import org.juiser.spring.security.user.SecurityContextUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.lang.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.function.Function;

/**
 * @since 0.1.0
 */
@SuppressWarnings({"SpringJavaAutowiringInspection", "SpringAutowiredFieldsWarningInspection"})
@Configuration
@ConditionalOnProperty(name = {"juiser.enabled"}, matchIfMissing = true)
@ConditionalOnClass({AbstractHttpConfigurer.class})
@ConditionalOnJuiserSpringSecurityEnabled
public class JuiserSecurityAutoConfiguration {

    @Autowired
    @Qualifier("juiserForwardedUserJwsClaimsExtractor")
    private Function<String, Claims> claimsExtractor;

    @Autowired
    @Qualifier("juiserJwtClaimsUserFactory")
    private Function<Claims, User> claimsUserFactory;

    @Bean
    @ConditionalOnMissingBean
    public ForwardedHeaderConfig<SpringSecurityJwtConfig> forwardedHeaderConfig() {
        return new ForwardedHeaderConfig<>(new SpringSecurityJwtConfig());
    }

    @Bean
    @ConditionalOnMissingBean(name = "juiserJwtDataExtractor")
    public Function<Claims, Collection<String>> juiserGrantedAuthoritiesClaimResolver() {

        final SpringSecurityJwtConfig jwt = forwardedHeaderConfig().getJwt();

        String expression = jwt.getGrantedAuthoritiesExpression();

        if (!Strings.hasText(expression)) {
            expression = "['user']['groups']['items'].![get('name')]";
        }

        return new StringCollectionClaimResolver(new ClaimsExpressionEvaluator(expression), false);
    }

    @Bean
    @ConditionalOnMissingBean(name = "juiserJwsUserDetailsConverter")
    public Function<String, UserDetails> juiserJwsUserDetailsConverter() {

        Function<Claims, Collection<? extends GrantedAuthority>> grantedAuthoritiesResolver =
            new ClaimsGrantedAuthoritiesResolver(juiserGrantedAuthoritiesClaimResolver());

        return new JwsToUserDetailsConverter(claimsExtractor, claimsUserFactory, grantedAuthoritiesResolver);
    }

    @Bean
    @ConditionalOnMissingBean(name = "juiserAuthenticationProvider")
    public AuthenticationProvider juiserAuthenticationProvider() {
        Function<String, UserDetails> converter = juiserJwsUserDetailsConverter();
        return new HeaderAuthenticationProvider(converter);
    }

    @Autowired
    public void registerAuthenticationProvider(AuthenticationManagerBuilder builder,
                                               @Qualifier("juiserAuthenticationProvider") AuthenticationProvider provider) {
        builder.authenticationProvider(provider);
    }

    @Bean
    @ConditionalOnMissingBean
    public User juiserUser() {
        return new SecurityContextUser();
    }
}
