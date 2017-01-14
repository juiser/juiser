package com.stormpath.juiser.spring.security.user;

import com.stormpath.juiser.model.Phone;
import com.stormpath.juiser.model.User;
import com.stormpath.juiser.spring.security.core.userdetails.ForwardedUserDetails;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @since 0.1.0
 */
public class SecurityContextUser implements User {

    protected SecurityContext getSecurityContext() {
        return SecurityContextHolder.getContext();
    }

    @Override
    public boolean isAuthenticated() {
        return getValidAuthentication() != null;
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

    protected User getRequiredAuthenticationUser() {
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

    @Override
    public boolean isAnonymous() {
        return !isAuthenticated();
    }

    @Override
    public String getHref() {
        return isAnonymous() ? null : getRequiredAuthenticationUser().getHref();
    }

    @Override
    public String getId() {
        return isAnonymous() ? null : getRequiredAuthenticationUser().getId();
    }

    @Override
    public String getName() {
        return isAnonymous() ? null : getRequiredAuthenticationUser().getName();
    }

    @Override
    public String getGivenName() {
        return isAnonymous() ? null : getRequiredAuthenticationUser().getGivenName();
    }

    @Override
    public String getFamilyName() {
        return isAnonymous() ? null : getRequiredAuthenticationUser().getFamilyName();
    }

    @Override
    public String getMiddleName() {
        return isAnonymous() ? null : getRequiredAuthenticationUser().getMiddleName();
    }

    @Override
    public String getNickname() {
        return isAnonymous() ? null : getRequiredAuthenticationUser().getNickname();
    }

    @Override
    public String getUsername() {
        return isAnonymous() ? null : getRequiredAuthenticationUser().getUsername();
    }

    @Override
    public URL getProfile() {
        return isAnonymous() ? null : getRequiredAuthenticationUser().getProfile();
    }

    @Override
    public URL getPicture() {
        return isAnonymous() ? null : getRequiredAuthenticationUser().getPicture();
    }

    @Override
    public URL getWebsite() {
        return isAnonymous() ? null : getRequiredAuthenticationUser().getWebsite();
    }

    @Override
    public String getEmail() {
        return isAnonymous() ? null : getRequiredAuthenticationUser().getEmail();
    }

    @Override
    public boolean isEmailVerified() {
        return isAuthenticated() && getRequiredAuthenticationUser().isEmailVerified();
    }

    @Override
    public String getGender() {
        return isAnonymous() ? null : getRequiredAuthenticationUser().getGender();
    }

    @Override
    public LocalDate getBirthdate() {
        return isAnonymous() ? null : getRequiredAuthenticationUser().getBirthdate();
    }

    @Override
    public TimeZone getZoneInfo() {
        return isAnonymous() ? null : getRequiredAuthenticationUser().getZoneInfo();
    }

    @Override
    public Locale getLocale() {
        return isAnonymous() ? null : getRequiredAuthenticationUser().getLocale();
    }

    @Override
    public Phone getPhone() {
        return isAnonymous() ? null : getRequiredAuthenticationUser().getPhone();
    }

    @Override
    public String getPhoneNumber() {
        return isAnonymous() ? null : getRequiredAuthenticationUser().getPhoneNumber();
    }

    @Override
    public boolean isPhoneNumberVerified() {
        return isAuthenticated() && getRequiredAuthenticationUser().isPhoneNumberVerified();
    }

    @Override
    public ZonedDateTime getCreatedAt() {
        return isAnonymous() ? null : getRequiredAuthenticationUser().getCreatedAt();
    }

    @Override
    public ZonedDateTime getUpdatedAt() {
        return isAnonymous() ? null : getRequiredAuthenticationUser().getUpdatedAt();
    }
}
