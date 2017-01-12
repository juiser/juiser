package com.stormpath.juiser.jwt;

import io.jsonwebtoken.Claims;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

/**
 * @since 0.1.0
 */
public abstract class ClaimCollectionResolver<T> extends ClaimValueResolver<Collection<T>> {

    public ClaimCollectionResolver(Function<Claims, ?> delegate, boolean resultRequired) {
        super(delegate, resultRequired);
    }

    @Override
    protected Collection<T> onNullValue() {
        return Collections.emptyList();
    }
}
