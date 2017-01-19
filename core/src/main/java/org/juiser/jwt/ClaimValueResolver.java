/*
 * Copyright (C) 2017 Stormpath, Inc.
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
package org.juiser.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.lang.Assert;

import java.util.function.Function;

/**
 * @since 0.1.0
 */
public abstract class ClaimValueResolver<T> implements Function<Claims, T> {

    private final boolean resultRequired;
    private final Function<Claims, ?> delegate;

    public ClaimValueResolver(Function<Claims, ?> delegate, boolean resultRequired) {
        Assert.notNull(delegate, "delegate function cannot be null.");
        this.delegate = delegate;
        this.resultRequired = resultRequired;
    }

    @Override
    public T apply(Claims claims) {
        Object v = delegate.apply(claims);

        if (v == null) {
            if (resultRequired) {
                illegal(claims);
            }
            return onNullValue();
        }

        return toTypedValue(v, claims);
    }

    protected T onNullValue() {
        return null;
    }

    protected abstract T toTypedValue(Object value, Claims claims);

    protected void illegal(Claims claims) {
        String msg = "Could not obtain value from claims {" + claims + "} using delegate: " + delegate;
        throw new IllegalArgumentException(msg);
    }
}
