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
package org.juiser.spring.boot.config;

import org.juiser.jwt.config.JwtConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

/**
 * @since 0.1.0
 */
@ConfigurationProperties("juiser.header")
public class ForwardedHeaderConfig<T extends JwtConfig> {

    @SuppressWarnings("WeakerAccess")
    public static final String DEFAULT_NAME = "X-Forwarded-User";

    private String name;

    private T jwt;

    @SuppressWarnings("unchecked")
    public ForwardedHeaderConfig() {
        this((T) new JwtConfig());
    }

    public ForwardedHeaderConfig(T jwt) {
        this.name = DEFAULT_NAME;
        setJwt(jwt);
    }

    public String getName() {
        return name;
    }

    public ForwardedHeaderConfig<T> setName(String name) {
        Assert.hasText(name, "jwt header name cannot be null or empty.");
        this.name = name;
        return this;
    }

    public T getJwt() {
        return jwt;
    }

    public ForwardedHeaderConfig<T> setJwt(T jwt) {
        Assert.notNull(jwt, "jwt config argument cannot be null.");
        this.jwt = jwt;
        return this;
    }
}
