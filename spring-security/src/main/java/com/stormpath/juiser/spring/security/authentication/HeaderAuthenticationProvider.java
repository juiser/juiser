package com.stormpath.juiser.spring.security.authentication;

import com.stormpath.juiser.spring.security.core.ForwardedUserAuthentication;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.function.Function;

/**
 * @since 0.1.0
 */
public class HeaderAuthenticationProvider implements AuthenticationProvider {

    private final Function<String, UserDetails> converter;

    public HeaderAuthenticationProvider(Function<String, UserDetails> converter) {
        Assert.notNull(converter, "converter function cannot be null.");
        this.converter = converter;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return HeaderAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        HeaderAuthenticationToken token = (HeaderAuthenticationToken) authentication;

        Object creds = token.getCredentials();
        if (!(creds instanceof String)) {
            throw new BadCredentialsException("HeaderAuthenticationToken credentials must be a String.");
        }

        String value = (String) creds;
        if (!StringUtils.hasText(value)) {
            throw new BadCredentialsException("HeaderAuthenticationToken credentials cannot be null or empty.");
        }

        UserDetails details = converter.apply(value);

        return new ForwardedUserAuthentication(details);
    }
}
