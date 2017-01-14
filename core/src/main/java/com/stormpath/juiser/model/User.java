package com.stormpath.juiser.model;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.TimeZone;

/**
 * User representation suitable for most applications.  Most fields are based on
 * <a href="http://openid.net/specs/openid-connect-core-1_0.html#StandardClaims">OpenID Connect Standard Claims</a>.
 *
 * @since 0.1.0
 */
public interface User {

    /**
     * Returns {@code true} if the user has been authenticated (proven their identity) or {@code false} if the user
     * is considered anonymous.
     *
     * @return {@code true} if the user has been authenticated (proven their identity) or {@code false} if the user
     * is considered anonymous.
     */
    boolean isAuthenticated();

    /**
     * Returns {@code true} if the user identity is unknown, {@code false} if the user is authenticated.
     *
     * @return {@code true} if the user identity is unknown, {@code false} if the user is authenticated.
     */
    boolean isAnonymous();

    String getHref();

    String getId();

    String getName(); //full name

    String getGivenName(); //aka 'first name' in Western cultures

    String getFamilyName(); //aka surname

    String getMiddleName();

    String getNickname(); //aka 'Mike' if givenName is 'Michael'

    String getUsername(); //OIDC 'preferred_username'

    URL getProfile();

    URL getPicture();

    URL getWebsite();

    String getEmail();

    boolean isEmailVerified();

    String getGender();

    LocalDate getBirthdate();

    TimeZone getZoneInfo();

    Locale getLocale();

    Phone getPhone();

    String getPhoneNumber();

    boolean isPhoneNumberVerified();

    ZonedDateTime getCreatedAt();

    ZonedDateTime getUpdatedAt();
}
