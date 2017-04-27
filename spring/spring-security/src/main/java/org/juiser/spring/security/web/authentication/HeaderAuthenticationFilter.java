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
package org.juiser.spring.security.web.authentication;

import org.juiser.spring.security.authentication.HeaderAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @since 0.1.0
 */
public class HeaderAuthenticationFilter extends GenericFilterBean {

    private static final Logger log = LoggerFactory.getLogger(HeaderAuthenticationFilter.class);

    private final String headerName;

    private final AuthenticationManager authenticationManager;

    private final RequestMatcher requiresAuthenticationRequestMatcher;

    public HeaderAuthenticationFilter(String headerName, AuthenticationManager authenticationManager) {
        Assert.hasText(headerName, "headerName cannot be null or empty.");
        Assert.notNull("AuthenticationManager is required.");
        this.headerName = headerName;
        this.requiresAuthenticationRequestMatcher = new RequestHeaderRequestMatcher(headerName, null);
        this.authenticationManager = authenticationManager;
    }

    protected String getHeaderName() {
        return headerName;
    }

    protected AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    protected RequestMatcher getRequiresAuthenticationRequestMatcher() {
        return requiresAuthenticationRequestMatcher;
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        Assert.notNull(authenticationManager, "authenticationManager must be specified");
    }

    /**
     * Indicates whether this filter should attempt to process a login request for the
     * current invocation.
     * <p>
     * It strips any parameters from the "path" section of the request URL (such as the
     * jsessionid parameter in <em>http://host/myapp/index.html;jsessionid=blah</em>)
     * before matching against the <code>filterProcessesUrl</code> property.
     * <p>
     * Subclasses may override for special requirements, such as Tapestry integration.
     *
     * @return <code>true</code> if the filter should attempt authentication,
     * <code>false</code> otherwise.
     */
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return getRequiresAuthenticationRequestMatcher().matches(request);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (!requiresAuthentication(request, response)) {
            chain.doFilter(request, response);
            return;
        }

        log.debug("Request will be authenticated.");

        boolean continueChain = true;
        Authentication authResult = null;

        try {
            authResult = attemptAuthentication(request, response);
            Assert.notNull(authResult, "authResult cannot be null.");
        } catch (InternalAuthenticationServiceException failed) {
            log.error("An internal error occurred while trying to authenticate the request header. Request will be anonymous.", failed);
            continueChain = unsuccessfulAuthentication(request, response, chain, failed);
        } catch (AuthenticationException failed) {
            // Authentication failed
            continueChain = unsuccessfulAuthentication(request, response, chain, failed);
        }

        if (authResult != null) {
            continueChain = successfulAuthentication(request, response, chain, authResult);
        }

        if (continueChain) {
            chain.doFilter(request, response);
        }
    }

    protected boolean successfulAuthentication(HttpServletRequest request,
                                               HttpServletResponse response,
                                               FilterChain chain,
                                               Authentication authResult)
        throws IOException, ServletException {

        log.debug("Request authenticated.  Updating SecurityContextHolder to contain: {}", authResult);

        SecurityContextHolder.getContext().setAuthentication(authResult);

        return true;
    }

    /**
     * Clears the {@link SecurityContextHolder} and returns {@code true}.
     */
    protected boolean unsuccessfulAuthentication(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                 FilterChain chain,
                                                 AuthenticationException failed)
        throws IOException, ServletException {

        SecurityContextHolder.clearContext();

        if (log.isDebugEnabled()) {
            log.debug("Authentication request failed: " + failed.toString(), failed);
            log.debug("Updated SecurityContextHolder to contain null Authentication");
            log.debug("Continuing filter chain with null Authentication");
        }

        return true;
    }

    protected Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException, IOException, ServletException {

        String name = getHeaderName();

        String value = request.getHeader(name);

        Assert.hasText(value, "Missing expected request header value.");

        HeaderAuthenticationToken token = new HeaderAuthenticationToken(name, value);

        return getAuthenticationManager().authenticate(token);
    }
}
