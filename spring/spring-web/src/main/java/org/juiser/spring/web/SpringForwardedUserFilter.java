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
package org.juiser.spring.web;

import org.juiser.model.User;
import org.juiser.servlet.ForwardedUserFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.function.Function;

/**
 * @since 0.1.0
 */
public class SpringForwardedUserFilter extends OncePerRequestFilter {

    private final ForwardedUserFilter delegate;

    public SpringForwardedUserFilter(String headerName,
                                     Function<HttpServletRequest, User> userFactory,
                                     Collection<String> requestAttributeNames) {
        this.delegate = new ForwardedUserFilter(headerName, userFactory, requestAttributeNames);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !this.delegate.isEnabled(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        this.delegate.doFilter(request, response, filterChain);
    }
}
