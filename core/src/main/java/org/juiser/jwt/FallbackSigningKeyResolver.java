/*
 * Copyright 2017 Les Hazlewood and the respective Juiser contributors
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
