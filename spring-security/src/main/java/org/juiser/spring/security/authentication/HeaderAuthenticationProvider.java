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
package org.juiser.spring.security.authentication;

import org.juiser.spring.security.core.ForwardedUserAuthentication;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.function.Function;

/**
 * @since 0.1.0
 */
public class HeaderAuthenticationProvider implements AuthenticationProvider {

    private final Function<String, UserDetails> converter;

    public HeaderAuthenticationProvider(Function<String, UserDetails> converter) {
        Assert.notNull(converter, "converter function cannot be null.");
        this.converter = converter;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return HeaderAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        HeaderAuthenticationToken token = (HeaderAuthenticationToken) authentication;

        Object creds = token.getCredentials();
        if (!(creds instanceof String)) {
            throw new BadCredentialsException("HeaderAuthenticationToken credentials must be a String.");
        }

        String value = (String) creds;
        if (!StringUtils.hasText(value)) {
            throw new BadCredentialsException("HeaderAuthenticationToken credentials cannot be null or empty.");
        }

        UserDetails details = converter.apply(value);

        return new ForwardedUserAuthentication(details);
    }
}
