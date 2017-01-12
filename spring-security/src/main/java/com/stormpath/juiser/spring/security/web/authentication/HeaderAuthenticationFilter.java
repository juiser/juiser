package com.stormpath.juiser.spring.security.web.authentication;

import com.stormpath.juiser.spring.security.authentication.HeaderAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @since 0.1.0
 */
public class HeaderAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final String headerName;

    public HeaderAuthenticationFilter(String headerName) {
        super(new RequestHeaderRequestMatcher(headerName, null));
        Assert.hasText(headerName, "headerName cannot be null or empty.");
        this.headerName = headerName;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException, IOException, ServletException {

        String name = headerName;

        String value = request.getHeader(name);

        Assert.hasText(value, "Missing expected request header value.");

        HeaderAuthenticationToken token = new HeaderAuthenticationToken(name, value);

        return getAuthenticationManager().authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
        throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }
}
