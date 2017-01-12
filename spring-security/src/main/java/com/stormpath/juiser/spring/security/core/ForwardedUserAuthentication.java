package com.stormpath.juiser.spring.security.core;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.util.Collection;

/**
 * @since 0.1.0
 */
public class ForwardedUserAuthentication implements Authentication {

    private final UserDetails details;

    public ForwardedUserAuthentication(UserDetails details) {
        Assert.notNull(details, "details argument cannot be null.");
        this.details = details;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return details.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return details;
    }

    @Override
    public Object getPrincipal() {
        return details;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new IllegalArgumentException(getClass().getName() + " instances are immutable.");
    }

    @Override
    public String getName() {
        return details.getUsername();
    }
}
