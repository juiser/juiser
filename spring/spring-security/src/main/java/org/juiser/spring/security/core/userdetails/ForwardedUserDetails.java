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
package org.juiser.spring.security.core.userdetails;

import org.juiser.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * @since 1.0.0
 */
public class ForwardedUserDetails implements UserDetails {

    private final User user;
    private final Collection<? extends GrantedAuthority> grantedAuthorities;

    public ForwardedUserDetails(User user, Collection<? extends GrantedAuthority> grantedAuthorities) {
        Assert.notNull(user, "User argument cannot be null.");
        Assert.hasText(user.getUsername(), "User username cannot be null or empty.");
        this.user = user;
        if (CollectionUtils.isEmpty(grantedAuthorities)) {
            this.grantedAuthorities = java.util.Collections.emptyList();
        } else {
            this.grantedAuthorities = java.util.Collections.unmodifiableCollection(grantedAuthorities);
        }
    }

    public User getUser() {
        return this.user;
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
        return getUser().getUsername();
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
