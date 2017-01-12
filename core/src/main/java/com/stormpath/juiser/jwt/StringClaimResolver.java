package com.stormpath.juiser.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.lang.Strings;

import java.util.function.Function;

/**
 * 0.1.0
 */
public class StringClaimResolver extends ClaimValueResolver<String> {

    public StringClaimResolver(Function<Claims, ?> delegate) {
        this(delegate, true);
    }

    public StringClaimResolver(Function<Claims, ?> delegate, boolean resultRequired) {
        super(delegate, resultRequired);
    }

    @Override
    protected String toTypedValue(Object v, Claims claims) {

        String value = String.valueOf(v);

        if (!Strings.hasText(value) || "null".equalsIgnoreCase(value)) {
            illegal(claims);
        }

        return value;
    }
}
