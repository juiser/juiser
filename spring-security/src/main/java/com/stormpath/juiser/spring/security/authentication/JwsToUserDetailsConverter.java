package com.stormpath.juiser.spring.security.authentication;

import com.stormpath.juiser.model.User;
import com.stormpath.juiser.spring.security.core.userdetails.ForwardedUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.lang.Assert;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

/**
 * @since 0.1.0
 */
public class JwsToUserDetailsConverter implements Function<String, UserDetails> {

    private final Function<String, Claims> claimsExtractor;
    private final Function<Claims, User> claimsUserFactory;
    private final Function<Claims, Collection<? extends GrantedAuthority>> authoritiesResolver;

    public JwsToUserDetailsConverter(Function<String, Claims> claimsExtractor,
                                     Function<Claims, User> claimsUserFactory,
                                     Function<Claims, Collection<? extends GrantedAuthority>> authoritiesResolver) {
        Assert.notNull(claimsExtractor, "claimsExtractor cannot be null.");
        Assert.notNull(claimsUserFactory, "claimsUserFactory cannot be null.");
        this.claimsExtractor = claimsExtractor;
        this.claimsUserFactory = claimsUserFactory;
        this.authoritiesResolver = authoritiesResolver;
    }

    @Override
    public UserDetails apply(String headerValue) {

        Claims claims = claimsExtractor.apply(headerValue);

        User user = claimsUserFactory.apply(claims);

        Collection<? extends GrantedAuthority> authorities = Collections.emptyList();
        if (authoritiesResolver != null) {
            authorities = authoritiesResolver.apply(claims);
        }

        return new ForwardedUserDetails(user, authorities);
    }
}
