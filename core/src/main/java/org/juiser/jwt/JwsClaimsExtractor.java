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
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.lang.Assert;

import java.security.Key;
import java.util.function.Function;

/**
 * @since 0.1.0
 */
public class JwsClaimsExtractor implements Function<String, Claims> {

    private final Key signingKey;
    private final byte[] signingKeyBytes;
    private final SigningKeyResolver signingKeyResolver;
    private Long allowedClockSkewSeconds;

    public JwsClaimsExtractor() {
        this.signingKey = null;
        this.signingKeyBytes = null;
        this.signingKeyResolver = null;
        this.allowedClockSkewSeconds = null;
    }

    public JwsClaimsExtractor(byte[] hmacSigningKeyBytes) {
        Assert.isTrue(hmacSigningKeyBytes != null && hmacSigningKeyBytes.length > 0,
            "hmacSigningKeyByte array argument cannot be null or empty.");
        this.signingKeyBytes = hmacSigningKeyBytes;
        this.signingKey = null;
        this.signingKeyResolver = null;
    }

    public JwsClaimsExtractor(Key key) {
        Assert.notNull(key, "key argument cannot be null.");
        this.signingKey = key;
        this.signingKeyBytes = null;
        this.signingKeyResolver = null;
    }

    public JwsClaimsExtractor(SigningKeyResolver signingKeyResolver) {
        Assert.notNull(signingKeyResolver, "signingKeyResolver argument cannot be null.");
        this.signingKeyResolver = signingKeyResolver;
        this.signingKeyBytes = null;
        this.signingKey = null;
    }

    public JwsClaimsExtractor setAllowedClockSkewSeconds(Long allowedClockSkewSeconds) {
        this.allowedClockSkewSeconds = allowedClockSkewSeconds;
        return this;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public Claims apply(String headerValue) {
        JwtParser parser = Jwts.parser();

        if (signingKeyBytes != null) {
            parser.setSigningKey(signingKeyBytes);
        } else if (signingKey != null) {
            parser.setSigningKey(signingKey);
        } else if (signingKeyResolver != null) {
            parser.setSigningKeyResolver(signingKeyResolver);
        }

        if (this.allowedClockSkewSeconds != null) {
            parser.setAllowedClockSkewSeconds(this.allowedClockSkewSeconds);
        }

        return parser.parseClaimsJws(headerValue).getBody();
    }
}
