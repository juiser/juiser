package com.stormpath.juiser.spring.security.core.userdetails;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * @since 0.1.0
 */
public class ForwardedUserDetails<T> implements UserDetails {

    private final String username;
    private final T data;
    private final Collection<? extends GrantedAuthority> grantedAuthorities;

    public ForwardedUserDetails(String username, T data, Collection<? extends GrantedAuthority> grantedAuthorities) {
        Assert.hasText(username, "username argument cannot be null or empty.");
        this.username = username;
        this.data = data;
        if (CollectionUtils.isEmpty(grantedAuthorities)) {
            this.grantedAuthorities = java.util.Collections.emptyList();
        } else {
            this.grantedAuthorities = java.util.Collections.unmodifiableCollection(grantedAuthorities);
        }
    }

    public T getData() {
        return data;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; //gateway will not forward expired accounts
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; //gateway will not forward locked accounts
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; //gateway will not forward expired accounts
    }

    @Override
    public boolean isEnabled() {
        return true; //gateway will not forward disabled accounts
    }
}
