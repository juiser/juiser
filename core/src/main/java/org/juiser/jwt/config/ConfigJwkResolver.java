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
package org.juiser.jwt.config;

import org.juiser.io.DefaultResource;
import org.juiser.io.Resource;
import org.juiser.io.ResourceLoader;
import org.juiser.jwt.AlgorithmFamily;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import io.jsonwebtoken.lang.Assert;
import io.jsonwebtoken.lang.Classes;
import io.jsonwebtoken.lang.RuntimeEnvironment;
import io.jsonwebtoken.lang.Strings;

import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.PublicKey;
import java.security.interfaces.ECKey;
import java.security.interfaces.RSAKey;
import java.util.function.Function;

/**
 * @since 0.1.0
 */
public class ConfigJwkResolver implements Function<JwkConfig, Key> {

    private final ResourceLoader resourceLoader;

    public ConfigJwkResolver(ResourceLoader resourceLoader) {
        Assert.notNull(resourceLoader);
        this.resourceLoader = resourceLoader;
    }

    @Override
    public Key apply(JwkConfig jwk) {

        Key key = null;

        AlgorithmFamily algorithmFamily = null;
        String algFamilyName = jwk.getAlgFamily();
        if (Strings.hasText(algFamilyName)) {
            try {
                algorithmFamily = AlgorithmFamily.forName(algFamilyName);
            } catch (IllegalArgumentException e) {
                String msg = "Unsupported juiser.header.jwt.jwk.algFamily value: " + algFamilyName + ".  " +
                    "Please use only " + AlgorithmFamily.class.getName() + " enum names: " +
                    Strings.arrayToCommaDelimitedString(AlgorithmFamily.values());
                throw new IllegalArgumentException(msg, e);
            }
        }

        byte[] bytes = null;

        Resource keyResource = getResource(jwk);

        String keyString = jwk.getValue();

        boolean keyStringSpecified = Strings.hasText(keyString);

        if (keyResource != null && keyStringSpecified) {
            String msg = "Both the juiser.header.jwt.key.value and " +
                "juiser.header.jwt.key.resource properties may not be set simultaneously.  " +
                "Please choose one.";
            throw new IllegalArgumentException(msg);
        }

        if (keyStringSpecified) {

            String encoding = jwk.getEncoding();

            if (keyString.startsWith(PemResourceKeyResolver.PEM_PREFIX)) {
                encoding = "pem";
            }

            if (encoding == null) {
                //default to the JWK specification format:
                encoding = "base64url";
            }

            if (encoding.equalsIgnoreCase("base64url")) {
                bytes = TextCodec.BASE64URL.decode(keyString);
            } else if (encoding.equalsIgnoreCase("base64")) {
                bytes = TextCodec.BASE64.decode(keyString);
            } else if (encoding.equalsIgnoreCase("utf8")) {
                bytes = keyString.getBytes(StandardCharsets.UTF_8);
            } else if (encoding.equalsIgnoreCase("pem")) {
                byte[] resourceBytes = keyString.getBytes(StandardCharsets.UTF_8);
                final ByteArrayInputStream bais = new ByteArrayInputStream(resourceBytes);
                keyResource = new DefaultResource(bais, "juiser.header.jwt.key.value");
            } else {
                throw new IllegalArgumentException("Unsupported encoding '" + encoding + "'.  Supported " +
                    "encodings: base64url, base64, utf8, pem.");
            }
        }

        if (bytes != null && bytes.length > 0) { //symmetric key

            if (algorithmFamily == null) {
                algorithmFamily = AlgorithmFamily.HMAC;
            }

            if (!algorithmFamily.equals(AlgorithmFamily.HMAC)) {
                String algFam = algorithmFamily.name();
                String msg = "It appears that the juiser.header.jwt.key.value " +
                    "is a shared (symmetric) secret key, and this requires the " +
                    "juiser.header.jwt.key.algFamily value to equal HMAC. " +
                    "The specified juiser.header.jwt.key.algFamily value is " + algFam + ". " +
                    "If you wish to use the " + algFam + " algorithm, please ensure that either 1) " +
                    "juiser.header.jwt.key.value is a public asymmetric PEM-encoded string, " +
                    "or 2) set the juiser.header.jwt.key.resource property to a " +
                    "Resource path where the PEM-encoded public key file resides, or " +
                    "or 3) define a bean named 'juiserForwardedAccountJwtSigningKey' that returns an " +
                    algFam + " private key instance.";
                throw new IllegalArgumentException(msg);
            }

            SignatureAlgorithm alg = getAlgorithm(bytes);
            key = new SecretKeySpec(bytes, alg.getJcaName());
        }

        if (keyResource != null) {

            Function<Resource, Key> resourceKeyResolver = createResourceKeyFunction(keyResource, keyStringSpecified);
            Assert.notNull(resourceKeyResolver, "resourceKeyResolver instance cannot be null.");
            key = resourceKeyResolver.apply(keyResource);
            if (key == null) {
                String msg = "Resource to Key resolver/function did not return a key for specified resource [" +
                    keyResource + "].  If providing your own implementation of this function, ensure it does not " +
                    "return null.";
                throw new IllegalStateException(msg);
            }

            Assert.notNull(key, "ResourceKeyResolver function did not return a key for specified resource [" + keyResource + "]");

            if (algorithmFamily == null) {
                if (key instanceof RSAKey) {
                    algorithmFamily = AlgorithmFamily.RSA;
                } else if (key instanceof ECKey) {
                    algorithmFamily = AlgorithmFamily.EC;
                } else {
                    String msg = "Unable to detect jwt signing key type to provide a default signature " +
                        "algorithm.  Please specify the juiser.header.jwt.key.algFamily property.";
                    throw new IllegalArgumentException(msg);
                }
            }

            if (key instanceof RSAKey && !algorithmFamily.equals(AlgorithmFamily.RSA)) {
                String msg = "Signature algorithm family [" + algorithmFamily + "] is not " +
                    "compatible with the specified RSA key.";
                throw new IllegalArgumentException(msg);
            }

            if (key instanceof ECKey && !algorithmFamily.equals(AlgorithmFamily.EC)) {
                String msg = "Signature algorithm family [" + algorithmFamily + "] is not " +
                    "compatible with the specified Elliptic Curve key.";
                throw new IllegalArgumentException(msg);
            }

            Assert.isTrue(key instanceof PublicKey, "Specified asymmetric signature verification key is not a " +
                "PublicKey.  Please ensure you specify a public (not private) key.");
        }

        return key;
    }

    private Resource getResource(JwkConfig jwk) {
        if (jwk.isEnabled()) {
            final String value = jwk.getResource();
            if (Strings.hasText(value)) {
                try {
                    return this.resourceLoader.getResource(value);
                } catch (Exception e) {
                    String msg = "Unable to load juiser.header.jwt.key.resource [" + value + "].";
                    throw new IllegalArgumentException(msg, e);
                }
            }
        }
        return null;
    }

    protected boolean isClassAvailable(String fqcn) {
        return Classes.isAvailable(fqcn);
    }

    protected Function<Resource, Key> createResourceKeyFunction(Resource keyResource, boolean keyStringSpecified) {

        if (!isClassAvailable("org.bouncycastle.openssl.PEMParser")) {

            String msg = "The org.bouncycastle:bcpkix-jdk15on:1.56 artifact (or newer) must be in the " +
                "classpath to be able to parse the " +
                (keyStringSpecified ?
                    "juiser.header.jwt.key.value PEM-encoded value" :
                    "juiser.header.jwt.key.resource [" + keyResource + "].");
            throw new IllegalStateException(msg);
        } else {
            RuntimeEnvironment.enableBouncyCastleIfPossible();
        }

        return new PemResourceKeyResolver();
    }

    //package private on purpose
    static SignatureAlgorithm getAlgorithm(byte[] hmacSigningKeyBytes) {
        Assert.isTrue(hmacSigningKeyBytes != null && hmacSigningKeyBytes.length > 0,
            "hmacSigningBytes cannot be null or empty.");
        if (hmacSigningKeyBytes.length >= 64) {
            return SignatureAlgorithm.HS512;
        } else if (hmacSigningKeyBytes.length >= 48) {
            return SignatureAlgorithm.HS384;
        } else { //<= 32
            return SignatureAlgorithm.HS256;
        }
    }
}
