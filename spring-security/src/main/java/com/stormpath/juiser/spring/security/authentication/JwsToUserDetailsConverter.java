package com.stormpath.juiser.spring.security.authentication;

import com.stormpath.juiser.spring.security.core.userdetails.ForwardedUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.lang.Assert;
import io.jsonwebtoken.lang.Strings;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

/**
 * @since 0.1.0
 */
public class JwsToUserDetailsConverter implements Function<String, UserDetails> {

    private final Function<String, Claims> claimsExtractor;
    private final Function<Claims, ?> dataResolver;
    private final Function<Claims, String> usernameResolver;
    private final Function<Claims, Collection<? extends GrantedAuthority>> authoritiesResolver;

    public JwsToUserDetailsConverter(Function<String, Claims> claimsExtractor,
                                     Function<Claims, ?> dataResolver,
                                     Function<Claims, String> usernameResolver,
                                     Function<Claims, Collection<? extends GrantedAuthority>> authoritiesResolver) {
        Assert.notNull(claimsExtractor, "claimsExtractor cannot be null.");
        Assert.notNull(usernameResolver, "usernameResolver cannot be null.");
        this.dataResolver = dataResolver;
        this.claimsExtractor = claimsExtractor;
        this.usernameResolver = usernameResolver;
        this.authoritiesResolver = authoritiesResolver;
    }

    @Override
    public UserDetails apply(String headerValue) {

        Claims claims = claimsExtractor.apply(headerValue);

        String username = usernameResolver.apply(claims);
        if (!Strings.hasText(username)) { //fallback:
            username = claims.getSubject();
        }

        Object data = null;
        if (dataResolver != null) {
            data = dataResolver.apply(claims);
        }

        Collection<? extends GrantedAuthority> authorities = Collections.emptyList();
        if (authoritiesResolver != null) {
            authorities = authoritiesResolver.apply(claims);
        }

        if (!StringUtils.hasText(username)) {
            String msg = "Unable to discover username from header value.";
            throw new BadCredentialsException(msg);
        }

        return new ForwardedUserDetails<>(username, data, authorities);
    }
}
