package com.stormpath.juiser.spring.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.util.Assert;

/**
 * @since 0.1.0
 */
public class HeaderAuthenticationToken extends AbstractAuthenticationToken {

    private final String headerName;

    private final String headerValue;

    public HeaderAuthenticationToken(String headerName, String headerValue) {
        super(null);
        Assert.hasText(headerName, "headerName cannot be null or empty.");
        this.headerName = headerName;
        Assert.hasText(headerValue, "headerValue cannot be null or empty.");
        this.headerValue = headerValue;
    }

    public String getHeaderName() {
        return headerName;
    }

    @Override
    public Object getCredentials() {
        return this.headerValue;
    }

    @Override
    public Object getPrincipal() {
        return this.headerValue;
    }
}
