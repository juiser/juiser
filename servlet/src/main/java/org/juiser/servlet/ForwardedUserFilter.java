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
package org.juiser.servlet;

import io.jsonwebtoken.lang.Assert;
import io.jsonwebtoken.lang.Collections;
import io.jsonwebtoken.lang.Strings;
import org.juiser.model.User;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.function.Function;

/**
 * @since 0.1.0
 */
public class ForwardedUserFilter implements Filter {

    private final String headerName;
    private final Function<HttpServletRequest, User> userFactory;
    private final Collection<String> requestAttributeNames;

    public ForwardedUserFilter(String headerName,
                               Function<HttpServletRequest, User> userFactory,
                               Collection<String> requestAttributeNames) {
        Assert.hasText(headerName, "headerName cannot be null or empty.");
        Assert.notNull(userFactory, "userFactory function cannot be null.");

        this.headerName = headerName;
        this.userFactory = userFactory;

        //always ensure that the fully qualified interface name is accessible:
        LinkedHashSet<String> set = new LinkedHashSet<>();
        set.add(User.class.getName());
        if (!Collections.isEmpty(requestAttributeNames)) {
            set.addAll(requestAttributeNames);
        }
        this.requestAttributeNames = set;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        if (isEnabled(req)) {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) resp;
            doFilter(request, response, chain);
        } else {
            chain.doFilter(req, resp);
        }
    }

    public boolean isEnabled(ServletRequest request) throws ServletException {
        return request instanceof HttpServletRequest && isEnabled((HttpServletRequest) request);
    }

    public boolean isEnabled(HttpServletRequest request) throws ServletException {
        String headerValue = request.getHeader(headerName);
        return Strings.hasText(headerValue);
    }

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        String value = request.getHeader(headerName);
        assert Strings.hasText(value) : "request header value cannot be null or empty. Call isEnabled before calling doFilter";

        User user = userFactory.apply(request);
        assert user != null : "user instance returned from userFactory function cannot be null.";

        for (String requestAttributeName : requestAttributeNames) {
            request.setAttribute(requestAttributeName, user);
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            for (String requestAttributeName : requestAttributeNames) {
                Object object = request.getAttribute(requestAttributeName);
                if (user.equals(object)) {
                    //only remove the object if we put it there.  If someone downstream of this filter changed
                    //it to be something else, we shouldn't touch it:
                    request.removeAttribute(requestAttributeName);
                }
            }
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //no op
    }

    @Override
    public void destroy() {
        //no op
    }
}
