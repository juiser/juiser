package com.stormpath.juiser.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import io.jsonwebtoken.lang.Assert;

import java.security.Key;

/**
 * @since 0.1.0
 */
public class FallbackSigningKeyResolver extends SigningKeyResolverAdapter {

    private final SigningKeyResolver delegate;
    private final Key fallbackKey;

    public FallbackSigningKeyResolver(SigningKeyResolver delegate, Key fallbackKey) {
        Assert.notNull(delegate, "SigningKeyResolver argument cannot be null.");
        Assert.notNull(fallbackKey, "fallbackKey argument cannot be null.");
        this.delegate = delegate;
        this.fallbackKey = fallbackKey;
    }

    @Override
    public Key resolveSigningKey(JwsHeader header, Claims claims) {

        Key result = delegate.resolveSigningKey(header, claims);

        if (result == null) {
            result = this.fallbackKey;
        }

        return result;
    }

}
