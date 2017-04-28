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
package org.juiser.spring.security.authentication;

import org.juiser.model.User;
import org.juiser.spring.security.core.userdetails.ForwardedUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.lang.Assert;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

/**
 * @since 1.0.0
 */
public class JwsToUserDetailsConverter implements Function<String, UserDetails> {

    private final Function<String, Claims> claimsExtractor;
    private final Function<Claims, User> claimsUserFactory;
    private final Function<Claims, Collection<? extends GrantedAuthority>> authoritiesResolver;

    public JwsToUserDetailsConverter(Function<String, Claims> claimsExtractor,
                                     Function<Claims, User> claimsUserFactory,
                                     Function<Claims, Collection<? extends GrantedAuthority>> authoritiesResolver) {
        Assert.notNull(claimsExtractor, "claimsExtractor cannot be null.");
        Assert.notNull(claimsUserFactory, "claimsUserFactory cannot be null.");
        this.claimsExtractor = claimsExtractor;
        this.claimsUserFactory = claimsUserFactory;
        this.authoritiesResolver = authoritiesResolver;
    }

    @Override
    public UserDetails apply(String headerValue) {

        Claims claims = claimsExtractor.apply(headerValue);

        User user = claimsUserFactory.apply(claims);

        Collection<? extends GrantedAuthority> authorities = Collections.emptyList();
        if (authoritiesResolver != null) {
            authorities = authoritiesResolver.apply(claims);
        }

        return new ForwardedUserDetails(user, authorities);
    }
}
