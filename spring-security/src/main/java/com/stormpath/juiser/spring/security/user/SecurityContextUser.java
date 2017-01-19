package com.stormpath.juiser.spring.security.user;

import com.stormpath.juiser.model.ResolvingUser;
import com.stormpath.juiser.model.User;
import com.stormpath.juiser.spring.security.core.userdetails.ForwardedUserDetails;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

/**
 * @since 0.1.0
 */
public class SecurityContextUser extends ResolvingUser {

    protected SecurityContext getSecurityContext() {
        return SecurityContextHolder.getContext();
    }

    protected Authentication getValidAuthentication() {
        SecurityContext ctx = getSecurityContext();
        if (ctx != null) {
            Authentication authc = ctx.getAuthentication();
            if (authc != null && !(authc instanceof AnonymousAuthenticationToken) && authc.isAuthenticated()) {
                return authc;
            }
        }
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return getValidAuthentication() != null;
    }

    @Override
    protected User findUser() {
        Authentication authc = getValidAuthentication();
        Assert.notNull(authc, "Current SecurityContext Authentication cannot be null.");
        Object value = authc.getPrincipal();
        Assert.isInstanceOf(ForwardedUserDetails.class, value,
            "securityContext.getAuthentication().getPrincipal() must contain a ForwardedUserDetails instance.");
        User user = null;
        if (value instanceof ForwardedUserDetails) {
            ForwardedUserDetails details = (ForwardedUserDetails) value;
            user = details.getUser();
        }
        if (user != null) {
            return user;
        }
        String msg = "Unable to acquire required " + User.class.getName() +
            " instance from the current SecurityContext Authentication.";
        throw new IllegalStateException(msg);
    }
}
